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

import jakarta.ws.rs.core.Response
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream
import kotlin.time.Duration.Companion.days

@Service
class KeycloakAdminService(keycloakAdminConfig: KeycloakAdminConfig) : OidcAdminService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private final val keycloak: Keycloak =
        KeycloakBuilder.builder()
            .serverUrl(keycloakAdminConfig.url)
            .realm(keycloakAdminConfig.realm)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(keycloakAdminConfig.clientId)
            .clientSecret(keycloakAdminConfig.clientSecret)
            .build()

    private final val usersResource: UsersResource = keycloak.realm(keycloakAdminConfig.realm).users()

    override fun createUser(oidcUser: OidcUser): Subject {
        val user =
            UserRepresentation().let {
                it.isEnabled = oidcUser.enabled
                it.username = oidcUser.username
                it.email = oidcUser.email
                it
            }

        val response = usersResource.create(user)

        if (response.statusInfo.family == Response.Status.Family.CLIENT_ERROR ||
            response.statusInfo.family == Response.Status.Family.SERVER_ERROR
        ) {
            logResponseBody(response)
        }

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

    fun logResponseBody(response: Response) {
        try {
            val inputStream = response.entity as InputStream
            val body = inputStream.bufferedReader().use { it.readText() }
            logger.error("Failed to create keycloak user: {}", body)
        } catch (e: Exception) {
            logger.warn("Failed to get response body", e)
        }
    }
}
