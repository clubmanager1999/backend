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
package com.github.clubmanager1999.backend.role

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.error.ErrorCode
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.membership.MembershipTestData
import com.github.clubmanager1999.backend.role.RoleTestData.ID
import com.github.clubmanager1999.backend.role.RoleTestData.NAME
import com.github.clubmanager1999.backend.security.Permission
import com.github.clubmanager1999.backend.security.Permission.MANAGE_ROLES
import com.github.clubmanager1999.backend.security.withRole
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class RoleControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var roleService: RoleService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnRole() {
        `when`(roleService.get(ID)).thenReturn(RoleTestData.createExistingRole())

        mockMvc
            .perform(get("/api/roles/$ID").withRole(MANAGE_ROLES))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.permissions[0]").value(Permission.MANAGE_MEMBERS.name))
            .andExpect(jsonPath("$.holder.id").value(MemberTestData.ID))
            .andExpect(jsonPath("$.holder.userName").value(MemberTestData.USER_NAME))
            .andExpect(jsonPath("$.holder.firstName").value(MemberTestData.FIRST_NAME))
            .andExpect(jsonPath("$.holder.lastName").value(MemberTestData.LAST_NAME))
            .andExpect(jsonPath("$.holder.email").value(MemberTestData.EMAIL))
            .andExpect(jsonPath("$.holder.address.street").value(MemberTestData.STREET))
            .andExpect(jsonPath("$.holder.address.streetNumber").value(MemberTestData.STREET_NUMBER))
            .andExpect(jsonPath("$.holder.address.city").value(MemberTestData.CITY))
            .andExpect(jsonPath("$.holder.address.zip").value(MemberTestData.ZIP))
            .andExpect(jsonPath("$.holder.membership.id").value(MembershipTestData.ID))
            .andExpect(jsonPath("$.holder.membership.name").value(MembershipTestData.NAME))
            .andExpect(jsonPath("$.holder.membership.fee").value(MembershipTestData.FEE))
    }

    @Test
    fun shouldReturnRoles() {
        `when`(roleService.getAll())
            .thenReturn(listOf(RoleTestData.createExistingRole()))

        mockMvc
            .perform(get("/api/roles").withRole(MANAGE_ROLES))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].name").value(NAME))
            .andExpect(jsonPath("$[0].permissions[0]").value(Permission.MANAGE_MEMBERS.name))
            .andExpect(jsonPath("$[0].holder.id").value(MemberTestData.ID))
            .andExpect(jsonPath("$[0].holder.userName").value(MemberTestData.USER_NAME))
            .andExpect(jsonPath("$[0].holder.firstName").value(MemberTestData.FIRST_NAME))
            .andExpect(jsonPath("$[0].holder.lastName").value(MemberTestData.LAST_NAME))
            .andExpect(jsonPath("$[0].holder.email").value(MemberTestData.EMAIL))
            .andExpect(jsonPath("$[0].holder.address.street").value(MemberTestData.STREET))
            .andExpect(jsonPath("$[0].holder.address.streetNumber").value(MemberTestData.STREET_NUMBER))
            .andExpect(jsonPath("$[0].holder.address.city").value(MemberTestData.CITY))
            .andExpect(jsonPath("$[0].holder.address.zip").value(MemberTestData.ZIP))
            .andExpect(jsonPath("$[0].holder.membership.id").value(MembershipTestData.ID))
            .andExpect(jsonPath("$[0].holder.membership.name").value(MembershipTestData.NAME))
            .andExpect(jsonPath("$[0].holder.membership.fee").value(MembershipTestData.FEE))
    }

    @Test
    fun shouldCreateRole() {
        `when`(roleService.create(RoleTestData.createNewRole()))
            .thenReturn(RoleTestData.createRoleId())

        mockMvc
            .perform(
                post("/api/roles")
                    .withRole(MANAGE_ROLES)
                    .content(
                        objectMapper.writeValueAsString(RoleTestData.createExistingRole()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/roles/$ID"))
    }

    @Test
    fun shouldCreateMultipleRoles() {
        `when`(roleService.create(RoleTestData.createNewRole()))
            .thenReturn(RoleTestData.createRoleId())

        mockMvc
            .perform(
                post("/api/roles")
                    .withRole(MANAGE_ROLES)
                    .content(
                        objectMapper.writeValueAsString(RoleTestData.createExistingRole()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/roles/$ID"))

        mockMvc
            .perform(
                post("/api/roles")
                    .withRole(MANAGE_ROLES)
                    .content(
                        objectMapper.writeValueAsString(RoleTestData.createExistingRole()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/roles/$ID"))
    }

    @Test
    fun shouldUpdateUser() {
        `when`(roleService.update(ID, RoleTestData.createNewRole()))
            .thenReturn(RoleTestData.createRoleId())

        mockMvc
            .perform(
                put("/api/roles/$ID")
                    .withRole(MANAGE_ROLES)
                    .content(objectMapper.writeValueAsString(RoleTestData.createNewRole()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteUser() {
        mockMvc
            .perform(delete("/api/roles/$ID").withRole(MANAGE_ROLES))
            .andExpect(status().isNoContent)

        verify(roleService).delete(ID)
    }

    @Test
    fun shouldAddPermission() {
        mockMvc
            .perform(
                put("/api/roles/$ID/permissions")
                    .withRole(MANAGE_ROLES)
                    .content(objectMapper.writeValueAsString(RoleTestData.createNewPermission()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)

        verify(roleService).addPermission(ID, Permission.MANAGE_MEMBERS)
    }

    @Test
    fun shouldRemovePermission() {
        mockMvc
            .perform(delete("/api/roles/$ID/permissions/${Permission.MANAGE_MEMBERS}").withRole(MANAGE_ROLES))
            .andExpect(status().isNoContent)

        verify(roleService).removePermission(ID, Permission.MANAGE_MEMBERS)
    }

    @Test
    fun shouldSetHolder() {
        mockMvc
            .perform(
                put("/api/roles/$ID/holder")
                    .withRole(MANAGE_ROLES)
                    .content(objectMapper.writeValueAsString(MemberTestData.createMemberId()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)

        verify(roleService).setHolder(ID, MemberTestData.createMemberId())
    }

    @Test
    fun shouldRemoveHolder() {
        mockMvc
            .perform(delete("/api/roles/$ID/holder").withRole(MANAGE_ROLES))
            .andExpect(status().isNoContent)

        verify(roleService).removeHolder(ID)
    }

    @Test
    fun shouldFailOnInvalidBodyPost() {
        `when`(roleService.create(RoleTestData.createNewRole()))
            .thenReturn(RoleTestData.createRoleId())

        mockMvc
            .perform(
                post("/api/roles")
                    .withRole(MANAGE_ROLES)
                    .content(
                        objectMapper.writeValueAsString(RoleTestData.createEmptyNewRole()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.name").value("must not be blank"))
    }

    @Test
    fun shouldFailOnInvalidBodyPut() {
        `when`(roleService.update(RoleTestData.ID, RoleTestData.createNewRole()))
            .thenReturn(RoleTestData.createRoleId())

        mockMvc
            .perform(
                put("/api/roles/$ID")
                    .withRole(MANAGE_ROLES)
                    .content(
                        objectMapper.writeValueAsString(RoleTestData.createEmptyNewRole()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.name").value("must not be blank"))
    }

    @Test
    fun shouldFailOnEmptyBodyPost() {
        `when`(roleService.create(RoleTestData.createNewRole()))
            .thenReturn(RoleTestData.createRoleId())

        mockMvc
            .perform(
                post("/api/roles")
                    .withRole(MANAGE_ROLES)
                    .content(
                        objectMapper.writeValueAsString(emptyMap<Void, Void>()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.name").value("must not be null"))
    }

    @Test
    fun shouldFailOnEmptyBodyPut() {
        `when`(roleService.update(ID, RoleTestData.createNewRole()))
            .thenReturn(RoleTestData.createRoleId())

        mockMvc
            .perform(
                put("/api/roles/$ID")
                    .withRole(MANAGE_ROLES)
                    .content(
                        objectMapper.writeValueAsString(emptyMap<Void, Void>()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.name").value("must not be null"))
    }
}
