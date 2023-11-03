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
package com.github.clubmanager1999.backend.roles

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.oidc.OidcAdminService
import com.github.clubmanager1999.backend.oidc.OidcTestData
import com.github.clubmanager1999.backend.oidc.OidcTestData.ROLE
import com.github.clubmanager1999.backend.security.Permission
import com.github.clubmanager1999.backend.security.withRole
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class RoleControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var oidcAdminService: OidcAdminService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnRole() {
        `when`(oidcAdminService.getRole(ROLE)).thenReturn(OidcTestData.createOidcRole())

        mockMvc
            .perform(get("/api/roles/$ROLE").withRole(Permission.MANAGE_ROLES))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(ROLE))
            .andExpect(jsonPath("$.permissions[0]").value(Permission.MANAGE_MEMBERS.name))
    }

    @Test
    fun shouldReturnRoles() {
        `when`(oidcAdminService.getRoles()).thenReturn(listOf(OidcTestData.createOidcRole()))

        mockMvc
            .perform(get("/api/roles").withRole(Permission.MANAGE_ROLES))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value(ROLE))
            .andExpect(jsonPath("$[0].permissions[0]").value(Permission.MANAGE_MEMBERS.name))
    }

    @Test
    fun shouldCreateRole() {
        mockMvc
            .perform(
                post("/api/roles").withRole(Permission.MANAGE_ROLES)
                    .content(objectMapper.writeValueAsString(NewRole(ROLE)))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/roles/$ROLE"))

        verify(oidcAdminService).createRole(ROLE)
    }

    @Test
    fun shouldCreateMultipleRoles() {
        mockMvc
            .perform(
                post("/api/roles").withRole(Permission.MANAGE_ROLES)
                    .content(objectMapper.writeValueAsString(NewRole(ROLE)))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/roles/$ROLE"))

        mockMvc
            .perform(
                post("/api/roles").withRole(Permission.MANAGE_ROLES)
                    .content(objectMapper.writeValueAsString(NewRole(ROLE)))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/roles/$ROLE"))

        verify(oidcAdminService, times(2)).createRole(ROLE)
    }

    @Test
    fun shouldAddPermission() {
        mockMvc
            .perform(
                post("/api/roles/$ROLE/permissions").withRole(Permission.MANAGE_ROLES)
                    .content(objectMapper.writeValueAsString(NewPermission(Permission.MANAGE_MEMBERS)))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)

        verify(oidcAdminService).addPermission(ROLE, Permission.MANAGE_MEMBERS)
    }

    @Test
    fun shouldRemovePermission() {
        mockMvc
            .perform(delete("/api/roles/$ROLE/permissions/${Permission.MANAGE_MEMBERS.name}").withRole(Permission.MANAGE_ROLES))
            .andExpect(status().isNoContent)

        verify(oidcAdminService).removePermission(ROLE, Permission.MANAGE_MEMBERS)
    }

    @Test
    fun shouldDeleteRole() {
        mockMvc
            .perform(delete("/api/roles/$ROLE").withRole(Permission.MANAGE_ROLES))
            .andExpect(status().isNoContent)

        verify(oidcAdminService).deleteRole(ROLE)
    }
}
