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
package com.github.clubmanager1999.backend.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class KeycloakJwtConverter(private val config: KeycloakJwtConfig) :
    Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(source: Jwt): AbstractAuthenticationToken {
        val name = source.getClaim<String>("sub")
        val resourceAccess: Map<String, Map<String, List<String>>>? = source.getClaim("resource_access")

        val roles =
            resourceAccess
                ?.get(config.clientName)
                ?.get("roles")
                ?.map { it.uppercase() }
                ?.map { it.replace("-", "_") }
                ?.map { SimpleGrantedAuthority("ROLE_$it") }

        return JwtAuthenticationToken(source, roles, name)
    }
}
