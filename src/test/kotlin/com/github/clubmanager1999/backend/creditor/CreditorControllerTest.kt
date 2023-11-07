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
package com.github.clubmanager1999.backend.creditor

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.creditor.CreditorTestData.ID
import com.github.clubmanager1999.backend.creditor.CreditorTestData.NAME
import com.github.clubmanager1999.backend.security.Permission.MANAGE_CREDITORS
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
internal class CreditorControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var creditorService: CreditorService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnCreditor() {
        `when`(creditorService.get(ID)).thenReturn(CreditorTestData.createExistingCreditor())

        mockMvc
            .perform(get("/api/creditors/$ID").withRole(MANAGE_CREDITORS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.name").value(NAME))
    }

    @Test
    fun shouldReturnCreditors() {
        `when`(creditorService.getAll())
            .thenReturn(listOf(CreditorTestData.createExistingCreditor()))

        mockMvc
            .perform(get("/api/creditors").withRole(MANAGE_CREDITORS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].name").value(NAME))
    }

    @Test
    fun shouldCreateCreditor() {
        `when`(creditorService.create(CreditorTestData.createNewCreditor()))
            .thenReturn(CreditorTestData.createExistingCreditor())

        mockMvc
            .perform(
                post("/api/creditors")
                    .withRole(MANAGE_CREDITORS)
                    .content(
                        objectMapper.writeValueAsString(CreditorTestData.createExistingCreditor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/creditors/$ID"))
    }

    @Test
    fun shouldCreateMultipleCreditors() {
        `when`(creditorService.create(CreditorTestData.createNewCreditor()))
            .thenReturn(CreditorTestData.createExistingCreditor())

        mockMvc
            .perform(
                post("/api/creditors")
                    .withRole(MANAGE_CREDITORS)
                    .content(
                        objectMapper.writeValueAsString(CreditorTestData.createExistingCreditor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/creditors/$ID"))

        mockMvc
            .perform(
                post("/api/creditors")
                    .withRole(MANAGE_CREDITORS)
                    .content(
                        objectMapper.writeValueAsString(CreditorTestData.createExistingCreditor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/creditors/$ID"))
    }

    @Test
    fun shouldUpdateCreditor() {
        `when`(creditorService.update(ID, CreditorTestData.createNewCreditor()))
            .thenReturn(CreditorTestData.createExistingCreditor())

        mockMvc
            .perform(
                put("/api/creditors/$ID")
                    .withRole(MANAGE_CREDITORS)
                    .content(objectMapper.writeValueAsString(CreditorTestData.createNewCreditor()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteCreditor() {
        mockMvc
            .perform(delete("/api/creditors/$ID").withRole(MANAGE_CREDITORS))
            .andExpect(status().isNoContent)

        verify(creditorService).delete(ID)
    }
}
