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

import dasniko.testcontainers.keycloak.KeycloakContainer
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension::class)
@ContextConfiguration(initializers = [KeycloakAdminServiceTest.DataSourceInitializer::class])
class KeycloakAdminServiceTest {
    companion object {
        @Container
        val keycloak: KeycloakContainer =
            KeycloakContainer().withRealmImportFile("/realm.json")
    }

    class DataSourceInitializer : ApplicationContextInitializer<ConfigurableApplicationContext?> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "oidc.admin.keycloak.url=" + keycloak.authServerUrl,
            )
        }
    }

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Autowired lateinit var keycloakAdminService: KeycloakAdminService

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

        Assertions.assertThatThrownBy { keycloakAdminService.createUser(oidcUser) }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(409)
    }

    @Test
    fun shouldNotFailOnLogResponseBodyErrors() {
        val response = mock<Response>()
        `when`(response.entity).thenThrow(RuntimeException())
        keycloakAdminService.logResponseBody(response)
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

        Assertions.assertThatThrownBy { keycloakAdminService.updateUser(Subject("unknown"), oidcUser) }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }
}
