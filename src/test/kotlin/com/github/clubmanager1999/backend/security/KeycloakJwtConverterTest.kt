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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.clubmanager1999.backend.security.SecurityTestData.CLIENT
import com.github.clubmanager1999.backend.security.SecurityTestData.JWT
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

@ExtendWith(MockitoExtension::class)
class KeycloakJwtConverterTest {
    private val objectMapper = ObjectMapper()

    @Mock lateinit var jwt: Jwt

    @Mock lateinit var keycloakJwtConfig: KeycloakJwtConfig

    @InjectMocks lateinit var keycloakJwtConverter: KeycloakJwtConverter

    @Test
    fun shouldDecodeToken() {
        val claims: Map<String, Any> = objectMapper.readValue(JWT)

        `when`(keycloakJwtConfig.clientName).thenReturn(CLIENT)
        `when`(jwt.getClaim<Any>("sub")).thenReturn(claims["sub"])
        `when`(jwt.getClaim<Any>("resource_access")).thenReturn(claims["resource_access"])

        val token = keycloakJwtConverter.convert(jwt)

        assertThat(token.name).isEqualTo(SUBJECT)
        assertThat(token.authorities).isEqualTo(listOf(SimpleGrantedAuthority("ROLE_${Permission.MANAGE_MEMBERS}")))
    }

    @Test
    fun shouldNotFailOnMissingName() {
        val claims: Map<String, Any> = objectMapper.readValue(JWT)

        `when`(keycloakJwtConfig.clientName).thenReturn(CLIENT)
        `when`(jwt.getClaim<Any>("sub")).thenReturn(null)
        `when`(jwt.getClaim<Any>("resource_access")).thenReturn(claims["resource_access"])

        val token = keycloakJwtConverter.convert(jwt)

        assertThat(token.name).isNull()
        assertThat(token.authorities).isEqualTo(listOf(SimpleGrantedAuthority("ROLE_${Permission.MANAGE_MEMBERS}")))
    }

    @Test
    fun shouldNotFailOnMissingResourceRoles() {
        val claims: Map<String, Any> = objectMapper.readValue(JWT)

        `when`(jwt.getClaim<Any>("sub")).thenReturn(claims["sub"])
        `when`(jwt.getClaim<Any>("resource_access")).thenReturn(null)

        val token = keycloakJwtConverter.convert(jwt)

        assertThat(token.name).isEqualTo(SUBJECT)
        assertThat(token.authorities).isEmpty()
    }

    @Test
    fun shouldNotFailOnMissingClient() {
        val claims: Map<String, Any> = objectMapper.readValue(JWT)

        `when`(keycloakJwtConfig.clientName).thenReturn("invalid")
        `when`(jwt.getClaim<Any>("sub")).thenReturn(claims["sub"])
        `when`(jwt.getClaim<Any>("resource_access")).thenReturn(claims["resource_access"])

        val token = keycloakJwtConverter.convert(jwt)

        assertThat(token.name).isEqualTo(SUBJECT)
        assertThat(token.authorities).isEmpty()
    }

    @Test
    fun shouldNotFailOnMissingClientRoles() {
        val claims: Map<String, Any> = objectMapper.readValue(JWT)

        `when`(keycloakJwtConfig.clientName).thenReturn(CLIENT)
        `when`(jwt.getClaim<Any>("sub")).thenReturn(claims["sub"])
        `when`(jwt.getClaim<Any>("resource_access"))
            .thenReturn(mapOf(CLIENT to emptyMap<String, Any>()))

        val token = keycloakJwtConverter.convert(jwt)

        assertThat(token.name).isEqualTo(SUBJECT)
        assertThat(token.authorities).isEmpty()
    }
}
