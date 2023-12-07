/*
Copyright (C) 2023 github.com/clubmanager1999

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.clubmanager1999.backend.oidc

import com.github.clubmanager1999.backend.oidc.OidcTestData.CLIENT
import com.github.clubmanager1999.backend.oidc.OidcTestData.CLIENT_ID
import com.github.clubmanager1999.backend.oidc.OidcTestData.REALM
import com.github.clubmanager1999.backend.oidc.OidcTestData.ROLE
import com.github.clubmanager1999.backend.security.KeycloakJwtConfig
import com.github.clubmanager1999.backend.security.Permission
import dasniko.testcontainers.keycloak.KeycloakContainer
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import jakarta.ws.rs.WebApplicationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.keycloak.admin.client.resource.ClientResource
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.representations.idm.RealmRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ContextConfiguration(initializers = [KeycloakAdminServiceTest.DataSourceInitializer::class])
@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension::class)
class KeycloakAdminServiceTest {
    companion object {
        private const val PORT_SMTP = 1025
        private const val PORT_HTTP = 8025

        private val mailNet: Network = Network.newNetwork()

        @Container
        val keycloak: KeycloakContainer =
            KeycloakContainer().withRealmImportFile("/realm.json").withNetwork(mailNet)

        @Container
        val mailhog: GenericContainer<*> =
            GenericContainer("mailhog/mailhog")
                .withExposedPorts(PORT_SMTP, PORT_HTTP)
                .waitingFor(Wait.forHttp("/"))
                .withNetwork(mailNet)
                .withNetworkAliases("mailhog")
    }

    class DataSourceInitializer : ApplicationContextInitializer<ConfigurableApplicationContext?> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            setupSmtpServer()
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "oidc.admin.keycloak.url=" + keycloak.authServerUrl,
            )
        }

        private fun setupSmtpServer() {
            val smtpServer =
                mapOf(
                    "port" to PORT_SMTP.toString(),
                    "host" to "mailhog",
                    "from" to "admin@paper-street-soap.co",
                )

            val realmResource = keycloak.keycloakAdminClient.realm(REALM)
            val realmRepresentation = RealmRepresentation()
            realmRepresentation.smtpServer = smtpServer

            realmResource.update(realmRepresentation)
        }
    }

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @SpyBean private lateinit var keycloakJwtConfig: KeycloakJwtConfig

    @Autowired lateinit var keycloakAdminService: KeycloakAdminService

    @BeforeEach
    fun setUp() {
        RestAssured.baseURI = "http://${mailhog.host}"
        RestAssured.port = mailhog.getMappedPort(PORT_HTTP)
        RestAssured.basePath = "/api/v2"

        rolesResource
            .list(false)
            .filter { it.attributes?.get(ATTRIBUTE_MANAGED_BY)?.contains(CLIENT_ID) ?: false }
            .forEach {
                rolesResource.deleteRole(it.name)
            }
    }

    private final val realmResource: RealmResource = keycloak.keycloakAdminClient.realm(REALM)
    private final val rolesResource: RolesResource = realmResource.roles()
    private final val clientResource: ClientResource = realmResource.clients().get(CLIENT)

    @Test
    fun shouldCreateUser() {
        val usersResource = keycloak.keycloakAdminClient.realm("clubmanager1999").users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val oidcUser = OidcTestData.createOidcUser()
        val subject = keycloakAdminService.createUser(oidcUser)

        val user = usersResource.get(subject.id).toRepresentation()

        assertThat(user.id).isEqualTo(subject.id)
        assertThat(user.isEnabled).isTrue()
        assertThat(user.username).isEqualTo(oidcUser.username)
        assertThat(user.email).isEqualTo(oidcUser.email)
    }

    @Test
    fun shouldFailOnExistingUser() {
        val oidcUser = OidcTestData.createOidcUser()

        try {
            keycloakAdminService.createUser(oidcUser)
        } catch (e: WebApplicationException) {
            assertThat(e.response.status).isEqualTo(409)
        }

        assertThatThrownBy { keycloakAdminService.createUser(oidcUser) }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(409)
    }

    @Test
    fun shouldUpdateUser() {
        val usersResource = keycloak.keycloakAdminClient.realm("clubmanager1999").users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val oidcUser = OidcTestData.createOidcUser()
        val subject = keycloakAdminService.createUser(oidcUser)

        val newEmail = "robert.paulson@paper-street-soap.co"
        val newOidcUser = oidcUser.copy(email = newEmail)
        keycloakAdminService.updateUser(subject, newOidcUser)

        val user = usersResource.get(subject.id).toRepresentation()

        assertThat(user.id).isEqualTo(subject.id)
        assertThat(user.isEnabled).isTrue()
        assertThat(user.email).isEqualTo(newEmail)
    }

    @Test
    fun shouldFailOnMissingUser() {
        val oidcUser = OidcTestData.createOidcUser()

        assertThatThrownBy { keycloakAdminService.updateUser(Subject("unknown"), oidcUser) }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }

    @Test
    fun shouldDeleteUser() {
        val usersResource = keycloak.keycloakAdminClient.realm("clubmanager1999").users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val oidcUser = OidcTestData.createOidcUser()
        val subject = keycloakAdminService.createUser(oidcUser)

        keycloakAdminService.deleteUser(subject)

        assertThatThrownBy { usersResource.get(subject.id).toRepresentation() }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun shouldNotFailOnDeletingMissingUser() {
        val usersResource = keycloak.keycloakAdminClient.realm("clubmanager1999").users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        keycloakAdminService.deleteUser(Subject("unknown"))
    }

    @Test
    fun shouldResetPassword() {
        val usersResource = keycloak.keycloakAdminClient.realm("clubmanager1999").users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val oidcUser = OidcTestData.createOidcUser()
        val subject = keycloakAdminService.createUser(oidcUser)

        keycloakAdminService.resetPassword(subject)

        given()
            .`when`()
            .get("/messages")
            .then()
            .body("items[0].From.Mailbox", equalTo("admin"))
            .body("items[0].From.Domain", equalTo("paper-street-soap.co"))
            .body("items[0].To[0].Mailbox", equalTo("info"))
            .body("items[0].To[0].Domain", equalTo("paper-street-soap.co"))
            .body("items[0].MIME.Parts[0].Body", containsString("Verify Email"))
            .body("items[0].MIME.Parts[0].Body", containsString("Update Password"))
    }

    @Test
    fun shouldAddUserExclusivelyToRole() {
        val usersResource = keycloak.keycloakAdminClient.realm(REALM).users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val subject1 = keycloakAdminService.createUser(OidcTestData.createOidcUser())

        createRole(ROLE)

        keycloakAdminService.assignRoleExclusivelyToUser(subject1, ROLE)

        assertThat(
            usersResource.get(subject1.id).roles().realmLevel().listAll().map {
                it.name
            }.filter { !it.contains("default") },
        ).containsExactly(ROLE)

        assertThat(rolesResource.get(ROLE).userMembers.map { it.id }).containsExactly(subject1.id)

        val subject2 =
            keycloakAdminService.createUser(
                OidcTestData.createOidcUser().copy(username = "robert.paulson", email = "robert.paulson@paper-street-soap.co"),
            )

        keycloakAdminService.assignRoleExclusivelyToUser(subject2, ROLE)

        assertThat(
            usersResource.get(subject1.id).roles().realmLevel().listAll().map {
                it.name
            }.filter { !it.contains("default") },
        ).isEmpty()

        assertThat(
            usersResource.get(subject2.id).roles().realmLevel().listAll().map {
                it.name
            }.filter { !it.contains("default") },
        ).containsExactly(ROLE)

        assertThat(rolesResource.get(ROLE).userMembers.map { it.id }).containsExactly(subject2.id)
    }

    @Test
    fun shouldNotFailOnDuplicatingAddUserExclusivelyToRole() {
        val usersResource = keycloak.keycloakAdminClient.realm(REALM).users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val subject1 = keycloakAdminService.createUser(OidcTestData.createOidcUser())

        createRole(ROLE)

        keycloakAdminService.assignRoleExclusivelyToUser(subject1, ROLE)

        keycloakAdminService.assignRoleExclusivelyToUser(subject1, ROLE)

        assertThat(
            usersResource.get(subject1.id).roles().realmLevel().listAll().map {
                it.name
            }.filter { !it.contains("default") },
        ).containsExactly(ROLE)

        assertThat(rolesResource.get(ROLE).userMembers.map { it.id }).containsExactly(subject1.id)
    }

    @Test
    fun shouldThrowExceptionOnUnknownRoleInAddUserExclusivelyToRole() {
        val usersResource = keycloak.keycloakAdminClient.realm(REALM).users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val subject1 = keycloakAdminService.createUser(OidcTestData.createOidcUser())

        assertThatThrownBy { keycloakAdminService.assignRoleExclusivelyToUser(subject1, ROLE) }
            .isInstanceOf(RoleNotFoundException::class.java)

        assertThat(
            usersResource.get(subject1.id).roles().realmLevel().listAll().map {
                it.name
            }.filter { !it.contains("default") },
        ).isEmpty()
    }

    @Test
    fun shouldRemoveAllUsersFromRole() {
        val usersResource = keycloak.keycloakAdminClient.realm(REALM).users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val oidcUser = OidcTestData.createOidcUser()
        val subject = keycloakAdminService.createUser(oidcUser)

        createRole(ROLE)

        val role = rolesResource.get(ROLE).toRepresentation()

        usersResource.get(subject.id)
            .roles()
            .realmLevel()
            .add(listOf(role))

        keycloakAdminService.unassignExclusiveRole(ROLE)

        assertThat(rolesResource.get(ROLE).userMembers.map { it.id }).isEmpty()
    }

    @Test
    fun shouldThrowExceptionOnUnknownRoleInRemoveAllUsersFromRole() {
        val usersResource = keycloak.keycloakAdminClient.realm(REALM).users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        assertThatThrownBy { keycloakAdminService.unassignExclusiveRole(ROLE) }
            .isInstanceOf(RoleNotFoundException::class.java)
    }

    @Test
    fun shouldCreateRole() {
        keycloakAdminService.createRole(ROLE)

        assertThat(rolesResource.get(ROLE).toRepresentation().name).isEqualTo(ROLE)
    }

    @Test
    fun shouldAddPermission() {
        createRole(ROLE)

        keycloakAdminService.addPermission(ROLE, Permission.MANAGE_MEMBERS)

        assertThat(
            rolesResource.get(ROLE).getClientRoleComposites(CLIENT).map { it.name },
        ).contains(Permission.MANAGE_MEMBERS.getRoleName())
    }

    @Test
    fun shouldRemovePermission() {
        createRole(ROLE)
        addPermission(ROLE, Permission.MANAGE_MEMBERS)

        assertThat(
            rolesResource.get(ROLE).getClientRoleComposites(CLIENT).map { it.name },
        ).contains(Permission.MANAGE_MEMBERS.getRoleName())

        keycloakAdminService.removePermission(ROLE, Permission.MANAGE_MEMBERS)

        assertThat(
            rolesResource.get(ROLE).getClientRoleComposites(CLIENT).map { it.name },
        ).doesNotContain(Permission.MANAGE_MEMBERS.getRoleName())
    }

    @Test
    fun shouldDeleteRole() {
        createRole(ROLE)

        keycloakAdminService.deleteRole(ROLE)

        assertThatThrownBy { rolesResource.get(ROLE).toRepresentation() }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }

    @Test
    fun shouldRemoveRoleFromUser() {
        val usersResource = keycloak.keycloakAdminClient.realm(REALM).users()

        usersResource.list().forEach {
            assertThat(usersResource.delete(it.id).status).isEqualTo(HttpStatus.NO_CONTENT.value())
        }

        val oidcUser = OidcTestData.createOidcUser()
        val subject = keycloakAdminService.createUser(oidcUser)

        createRole(ROLE)
        val role = rolesResource.get(ROLE).toRepresentation()
        usersResource.get(subject.id).roles().realmLevel().add(listOf(role))

        keycloakAdminService.removeRoleFromUser(subject, ROLE)

        assertThat(
            usersResource.get(subject.id).roles().realmLevel().listAll().map { it.name }.filter { !it.contains("default") },
        ).isEmpty()
    }

    fun createRole(name: String) {
        rolesResource.create(createRoleRepresentation(name))
    }

    fun createRole(
        name: String,
        managedBy: String,
    ) {
        rolesResource.create(createRoleRepresentation(name, managedBy))
    }

    fun createRoleRepresentation(name: String): RoleRepresentation {
        return createRoleRepresentation(name, CLIENT_ID)
    }

    fun createRoleRepresentation(
        name: String,
        managedBy: String,
    ): RoleRepresentation {
        return RoleRepresentation().let {
            it.name = name
            it.singleAttribute(ATTRIBUTE_MANAGED_BY, managedBy)
            it
        }
    }

    fun addPermission(
        name: String,
        permission: Permission,
    ) {
        val foundPermissionRole = clientResource.roles().get(permission.getRoleName()).toRepresentation()
        rolesResource.get(name).addComposites(listOf(foundPermissionRole))
    }
}
