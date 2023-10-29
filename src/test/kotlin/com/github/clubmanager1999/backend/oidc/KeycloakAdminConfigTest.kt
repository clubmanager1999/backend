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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class KeycloakAdminConfigTest {
    @Autowired private lateinit var keycloakAdminConfig: KeycloakAdminConfig

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldLoadConfig() {
        assertThat(keycloakAdminConfig.url).isEqualTo("defined-in-test")
        assertThat(keycloakAdminConfig.realm).isEqualTo("clubmanager1999")
        assertThat(keycloakAdminConfig.clientId).isEqualTo("clubmanager1999-backend")
        assertThat(keycloakAdminConfig.clientSecret).isEqualTo("ct9rMuQiyTa3F7pgw64NPgi4EWoLtctX")
    }
}
