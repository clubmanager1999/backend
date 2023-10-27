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
package com.github.clubmanager1999.backend.member

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class MemberControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var memberService: MemberService

    @Test
    fun shouldReturnMember() {
        `when`(memberService.get(42)).thenReturn(MemberTestData.createExistingMember())

        mockMvc
            .perform(get("/api/members/42"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.userName").value(USER_NAME))
            .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(LAST_NAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.address.street").value(STREET))
            .andExpect(jsonPath("$.address.streetNumber").value(STREET_NUMBER))
            .andExpect(jsonPath("$.address.city").value(CITY))
            .andExpect(jsonPath("$.address.zip").value(ZIP))
    }

    @Test
    fun shouldReturnMembers() {
        `when`(memberService.getAll()).thenReturn(listOf(MemberTestData.createExistingMember()))

        mockMvc
            .perform(get("/api/members"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].userName").value(USER_NAME))
            .andExpect(jsonPath("$[0].firstName").value(FIRST_NAME))
            .andExpect(jsonPath("$[0].lastName").value(LAST_NAME))
            .andExpect(jsonPath("$[0].email").value(EMAIL))
            .andExpect(jsonPath("$[0].address.street").value(STREET))
            .andExpect(jsonPath("$[0].address.streetNumber").value(STREET_NUMBER))
            .andExpect(jsonPath("$[0].address.city").value(CITY))
            .andExpect(jsonPath("$[0].address.zip").value(ZIP))
    }

    @Test
    fun shouldCreateMember() {
        `when`(memberService.create(MemberTestData.createNewMember()))
            .thenReturn(MemberTestData.createExistingMember())

        mockMvc
            .perform(
                post("/api/members")
                    .content(objectMapper.writeValueAsString(MemberTestData.createExistingMember()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldUpdateUser() {
        `when`(memberService.update(42, MemberTestData.createNewMember()))
            .thenReturn(MemberTestData.createExistingMember())

        mockMvc
            .perform(
                put("/api/members/42")
                    .content(objectMapper.writeValueAsString(MemberTestData.createNewMember()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteUser() {
        mockMvc
            .perform(delete("/api/members/42"))
            .andExpect(status().isNoContent)

        verify(memberService).delete(42)
    }
}
