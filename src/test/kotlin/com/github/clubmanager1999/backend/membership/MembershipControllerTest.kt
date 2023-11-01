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
package com.github.clubmanager1999.backend.membership

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.security.Roles.MEMBER_ADMIN
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
internal class MembershipControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var membershipService: MembershipService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnMembership() {
        `when`(membershipService.get(43)).thenReturn(MembershipTestData.createExistingMembership())

        mockMvc
            .perform(get("/api/memberships/43").withRole(MEMBER_ADMIN))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.fee").value(FEE))
    }

    @Test
    fun shouldReturnMemberships() {
        `when`(membershipService.getAll())
            .thenReturn(listOf(MembershipTestData.createExistingMembership()))

        mockMvc
            .perform(get("/api/memberships").withRole(MEMBER_ADMIN))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].name").value(NAME))
            .andExpect(jsonPath("$[0].fee").value(FEE))
    }

    @Test
    fun shouldCreateMembership() {
        `when`(membershipService.create(MembershipTestData.createNewMembership()))
            .thenReturn(MembershipTestData.createExistingMembership())

        mockMvc
            .perform(
                post("/api/memberships")
                    .withRole(MEMBER_ADMIN)
                    .content(
                        objectMapper.writeValueAsString(MembershipTestData.createExistingMembership()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/memberships/$ID"))
    }

    @Test
    fun shouldUpdateUser() {
        `when`(membershipService.update(43, MembershipTestData.createNewMembership()))
            .thenReturn(MembershipTestData.createExistingMembership())

        mockMvc
            .perform(
                put("/api/memberships/43")
                    .withRole(MEMBER_ADMIN)
                    .content(objectMapper.writeValueAsString(MembershipTestData.createNewMembership()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteUser() {
        mockMvc
            .perform(delete("/api/memberships/43").withRole(MEMBER_ADMIN))
            .andExpect(status().isNoContent)

        verify(membershipService).delete(43)
    }
}
