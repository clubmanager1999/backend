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

import com.github.clubmanager1999.backend.security.KeycloakJwtConfig
import com.github.clubmanager1999.backend.security.Permission
import jakarta.ws.rs.WebApplicationException
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.ClientResource
import org.keycloak.admin.client.resource.ClientsResource
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RoleResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.days

const val ATTRIBUTE_MANAGED_BY = "managedBy"

@Service
class KeycloakAdminService(
    private val keycloakAdminConfig: KeycloakAdminConfig,
    private val keycloakJwtConfig: KeycloakJwtConfig,
) : OidcAdminService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private final val keycloak: Keycloak =
        KeycloakBuilder.builder()
            .serverUrl(keycloakAdminConfig.url)
            .realm(keycloakAdminConfig.realm)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(keycloakAdminConfig.clientId)
            .clientSecret(keycloakAdminConfig.clientSecret)
            .build()

    private final val realmResource: RealmResource = keycloak.realm(keycloakAdminConfig.realm)

    private final val usersResource: UsersResource = realmResource.users()

    private final val rolesResource: RolesResource = realmResource.roles()

    private final val clientsResource: ClientsResource = realmResource.clients()

    override fun createUser(oidcUser: OidcUser): Subject {
        val user =
            UserRepresentation().let {
                it.isEnabled = oidcUser.enabled
                it.username = oidcUser.username
                it.email = oidcUser.email
                it
            }

        val response = usersResource.create(user)

        val id = CreatedResponseUtil.getCreatedId(response)

        return Subject(id)
    }

    override fun updateUser(
        subject: Subject,
        oidcUser: OidcUser,
    ) {
        val user =
            UserRepresentation().let {
                it.isEnabled = oidcUser.enabled
                it.username = oidcUser.username
                it.email = oidcUser.email
                it
            }

        val userResource = usersResource.get(subject.id)

        userResource.update(user)
    }

    override fun deleteUser(subject: Subject) {
        usersResource.delete(subject.id)
    }

    override fun resetPassword(subject: Subject) {
        usersResource
            .get(subject.id)
            .executeActionsEmail(
                listOf("VERIFY_EMAIL", "UPDATE_PASSWORD"),
                3.days.inWholeSeconds.toInt(),
            )
    }

    override fun assignRoleExclusivelyToUser(
        subject: Subject,
        role: String,
    ) {
        val membersToRemove =
            getUsersInRole(role)
                .filter { it.id != subject.id }

        addUserToRole(subject, role)

        membersToRemove
            .forEach { removeRoleFromUser(Subject(it.id), role) }
    }

    override fun unassignExclusiveRole(role: String) {
        val membersToRemove = getUsersInRole(role)

        membersToRemove
            .forEach { removeRoleFromUser(Subject(it.id), role) }
    }

    private fun getUsersInRole(role: String): MutableList<UserRepresentation> {
        return withRole(rolesResource, role) {
            it.userMembers
        }
    }

    override fun createRole(name: String) {
        val roleRepresentation =
            RoleRepresentation().let {
                it.name = name
                it.singleAttribute(ATTRIBUTE_MANAGED_BY, keycloakAdminConfig.clientId)
                it
            }

        rolesResource.create(roleRepresentation)
    }

    override fun addPermission(
        name: String,
        permission: Permission,
    ) {
        val permissionRole = getClientRoleRepresentation(permission.getRoleName())

        rolesResource.get(name).addComposites(listOf(permissionRole))
    }

    override fun removePermission(
        name: String,
        permission: Permission,
    ) {
        val permissionRole = getClientRoleRepresentation(permission.getRoleName())

        rolesResource.get(name).deleteComposites(listOf(permissionRole))
    }

    override fun deleteRole(name: String) {
        rolesResource
            .deleteRole(name)
    }

    fun addUserToRole(
        subject: Subject,
        role: String,
    ) {
        val roleRepresentation = getRoleRepresentation(role)

        usersResource.get(subject.id)
            .roles()
            .realmLevel()
            .add(listOf(roleRepresentation))
    }

    fun removeRoleFromUser(
        subject: Subject,
        role: String,
    ) {
        val roleRepresentation = getRoleRepresentation(role)

        usersResource.get(subject.id)
            .roles()
            .realmLevel()
            .remove(listOf(roleRepresentation))
    }

    fun getRoleRepresentation(name: String): RoleRepresentation {
        return getRoleRepresentation(rolesResource, name)
    }

    fun getClientRoleRepresentation(name: String): RoleRepresentation {
        return getRoleRepresentation(getClientResource().roles(), name)
    }

    fun getRoleRepresentation(
        resource: RolesResource,
        name: String,
    ): RoleRepresentation {
        return withRole(resource, name) {
            it.toRepresentation()
        }
    }

    fun getClientResource(): ClientResource {
        val clientRepresentation = getClientRepresentation()

        return clientsResource.get(clientRepresentation.id)
    }

    fun getClientRepresentation(): ClientRepresentation {
        val clientId = keycloakJwtConfig.clientName

        return clientsResource
            .findByClientId(clientId)
            .firstOrNull()
            ?: throw ClientNotFoundException(clientId)
    }

    fun <T> withRole(
        resource: RolesResource,
        name: String,
        block: (RoleResource) -> T,
    ): T {
        try {
            return block(resource.get(name))
        } catch (e: WebApplicationException) {
            if (e.response.status == HttpStatus.NOT_FOUND.value()) {
                throw RoleNotFoundException(name)
            } else {
                throw e
            }
        }
    }
}
