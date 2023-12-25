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
package com.github.clubmanager1999.backend.domain.template

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.domain.template.TemplateTestData.ID
import com.github.clubmanager1999.backend.domain.template.TemplateTestData.NAME
import com.github.clubmanager1999.backend.error.ErrorCode
import com.github.clubmanager1999.backend.security.Permission.MANAGE_TEMPLATES
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
internal class TemplateControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var templateService: TemplateService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnTemplate() {
        `when`(templateService.get(ID)).thenReturn(TemplateTestData.createExistingTemplate())

        mockMvc
            .perform(get("/api/templates/$ID").withRole(MANAGE_TEMPLATES))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.name").value(NAME))
    }

    @Test
    fun shouldReturnTemplates() {
        `when`(templateService.getAll())
            .thenReturn(listOf(TemplateTestData.createExistingTemplate()))

        mockMvc
            .perform(get("/api/templates").withRole(MANAGE_TEMPLATES))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].name").value(NAME))
    }

    @Test
    fun shouldCreateTemplate() {
        `when`(templateService.create(TemplateTestData.createNewTemplate()))
            .thenReturn(TemplateTestData.createTemplateId())

        mockMvc
            .perform(
                post("/api/templates")
                    .withRole(MANAGE_TEMPLATES)
                    .content(
                        objectMapper.writeValueAsString(TemplateTestData.createExistingTemplate()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/templates/$ID"))
    }

    @Test
    fun shouldCreateMultipleTemplates() {
        `when`(templateService.create(TemplateTestData.createNewTemplate()))
            .thenReturn(TemplateTestData.createTemplateId())

        mockMvc
            .perform(
                post("/api/templates")
                    .withRole(MANAGE_TEMPLATES)
                    .content(
                        objectMapper.writeValueAsString(TemplateTestData.createExistingTemplate()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/templates/$ID"))

        mockMvc
            .perform(
                post("/api/templates")
                    .withRole(MANAGE_TEMPLATES)
                    .content(
                        objectMapper.writeValueAsString(TemplateTestData.createExistingTemplate()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/templates/$ID"))
    }

    @Test
    fun shouldUpdateUser() {
        `when`(templateService.update(ID, TemplateTestData.createNewTemplate()))
            .thenReturn(TemplateTestData.createTemplateId())

        mockMvc
            .perform(
                put("/api/templates/$ID")
                    .withRole(MANAGE_TEMPLATES)
                    .content(objectMapper.writeValueAsString(TemplateTestData.createNewTemplate()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteUser() {
        mockMvc
            .perform(delete("/api/templates/$ID").withRole(MANAGE_TEMPLATES))
            .andExpect(status().isNoContent)

        verify(templateService).delete(ID)
    }

    @Test
    fun shouldFailOnInvalidBodyPost() {
        `when`(templateService.create(TemplateTestData.createNewTemplate()))
            .thenReturn(TemplateTestData.createTemplateId())

        mockMvc
            .perform(
                post("/api/templates")
                    .withRole(MANAGE_TEMPLATES)
                    .content(
                        objectMapper.writeValueAsString(TemplateTestData.createEmptyNewTemplate()),
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
        `when`(templateService.update(ID, TemplateTestData.createNewTemplate()))
            .thenReturn(TemplateTestData.createTemplateId())

        mockMvc
            .perform(
                put("/api/templates/$ID")
                    .withRole(MANAGE_TEMPLATES)
                    .content(
                        objectMapper.writeValueAsString(TemplateTestData.createEmptyNewTemplate()),
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
        `when`(templateService.create(TemplateTestData.createNewTemplate()))
            .thenReturn(TemplateTestData.createTemplateId())

        mockMvc
            .perform(
                post("/api/templates")
                    .withRole(MANAGE_TEMPLATES)
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
        `when`(templateService.update(ID, TemplateTestData.createNewTemplate()))
            .thenReturn(TemplateTestData.createTemplateId())

        mockMvc
            .perform(
                put("/api/templates/$ID")
                    .withRole(MANAGE_TEMPLATES)
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
