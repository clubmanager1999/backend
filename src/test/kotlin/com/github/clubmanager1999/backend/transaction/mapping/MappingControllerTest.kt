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
package com.github.clubmanager1999.backend.transaction.mapping

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.member.CITY
import com.github.clubmanager1999.backend.member.EMAIL
import com.github.clubmanager1999.backend.member.FIRST_NAME
import com.github.clubmanager1999.backend.member.LAST_NAME
import com.github.clubmanager1999.backend.member.STREET
import com.github.clubmanager1999.backend.member.STREET_NUMBER
import com.github.clubmanager1999.backend.member.USER_NAME
import com.github.clubmanager1999.backend.member.ZIP
import com.github.clubmanager1999.backend.security.Permission.MANAGE_MAPPINGS
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
internal class MappingControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var mappingService: MappingService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnMapping() {
        `when`(mappingService.get(ID)).thenReturn(MappingTestData.createExistingMapping())

        mockMvc
            .perform(get("/api/mappings/$ID").withRole(MANAGE_MAPPINGS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.matcher").value(MATCHER))
            .andExpect(jsonPath("$.reference.member.id").value(com.github.clubmanager1999.backend.member.ID))
            .andExpect(jsonPath("$.reference.member.userName").value(USER_NAME))
            .andExpect(jsonPath("$.reference.member.firstName").value(FIRST_NAME))
            .andExpect(jsonPath("$.reference.member.lastName").value(LAST_NAME))
            .andExpect(jsonPath("$.reference.member.email").value(EMAIL))
            .andExpect(jsonPath("$.reference.member.address.street").value(STREET))
            .andExpect(jsonPath("$.reference.member.address.streetNumber").value(STREET_NUMBER))
            .andExpect(jsonPath("$.reference.member.address.city").value(CITY))
            .andExpect(jsonPath("$.reference.member.address.zip").value(ZIP))
            .andExpect(jsonPath("$.reference.member.membership.id").value(com.github.clubmanager1999.backend.membership.ID))
            .andExpect(jsonPath("$.reference.member.membership.name").value(com.github.clubmanager1999.backend.membership.NAME))
            .andExpect(jsonPath("$.reference.member.membership.fee").value(com.github.clubmanager1999.backend.membership.FEE))
    }

    @Test
    fun shouldReturnMappings() {
        `when`(mappingService.getAll())
            .thenReturn(listOf(MappingTestData.createExistingMapping()))

        mockMvc
            .perform(get("/api/mappings").withRole(MANAGE_MAPPINGS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].matcher").value(MATCHER))
            .andExpect(jsonPath("$[0].reference.member.id").value(com.github.clubmanager1999.backend.member.ID))
            .andExpect(jsonPath("$[0].reference.member.userName").value(USER_NAME))
            .andExpect(jsonPath("$[0].reference.member.firstName").value(FIRST_NAME))
            .andExpect(jsonPath("$[0].reference.member.lastName").value(LAST_NAME))
            .andExpect(jsonPath("$[0].reference.member.email").value(EMAIL))
            .andExpect(jsonPath("$[0].reference.member.address.street").value(STREET))
            .andExpect(jsonPath("$[0].reference.member.address.streetNumber").value(STREET_NUMBER))
            .andExpect(jsonPath("$[0].reference.member.address.city").value(CITY))
            .andExpect(jsonPath("$[0].reference.member.address.zip").value(ZIP))
            .andExpect(jsonPath("$[0].reference.member.membership.id").value(com.github.clubmanager1999.backend.membership.ID))
            .andExpect(jsonPath("$[0].reference.member.membership.name").value(com.github.clubmanager1999.backend.membership.NAME))
            .andExpect(jsonPath("$[0].reference.member.membership.fee").value(com.github.clubmanager1999.backend.membership.FEE))
    }

    @Test
    fun shouldCreateMapping() {
        `when`(mappingService.create(MappingTestData.createNewMapping()))
            .thenReturn(MappingTestData.createExistingMapping())

        mockMvc
            .perform(
                post("/api/mappings")
                    .withRole(MANAGE_MAPPINGS)
                    .content(
                        objectMapper.writeValueAsString(MappingTestData.createExistingMapping()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/mappings/$ID"))
    }

    @Test
    fun shouldCreateMultipleMappings() {
        `when`(mappingService.create(MappingTestData.createNewMapping()))
            .thenReturn(MappingTestData.createExistingMapping())

        mockMvc
            .perform(
                post("/api/mappings")
                    .withRole(MANAGE_MAPPINGS)
                    .content(
                        objectMapper.writeValueAsString(MappingTestData.createExistingMapping()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/mappings/$ID"))

        mockMvc
            .perform(
                post("/api/mappings")
                    .withRole(MANAGE_MAPPINGS)
                    .content(
                        objectMapper.writeValueAsString(MappingTestData.createExistingMapping()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/mappings/$ID"))
    }

    @Test
    fun shouldUpdateUser() {
        `when`(mappingService.update(ID, MappingTestData.createNewMapping()))
            .thenReturn(MappingTestData.createExistingMapping())

        mockMvc
            .perform(
                put("/api/mappings/$ID")
                    .withRole(MANAGE_MAPPINGS)
                    .content(objectMapper.writeValueAsString(MappingTestData.createNewMapping()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteUser() {
        mockMvc
            .perform(delete("/api/mappings/$ID").withRole(MANAGE_MAPPINGS))
            .andExpect(status().isNoContent)

        verify(mappingService).delete(ID)
    }
}
