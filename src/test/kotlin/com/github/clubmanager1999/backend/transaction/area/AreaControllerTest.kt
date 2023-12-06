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
package com.github.clubmanager1999.backend.transaction.area

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.error.ErrorCode
import com.github.clubmanager1999.backend.security.Permission.MANAGE_AREAS
import com.github.clubmanager1999.backend.security.withRole
import com.github.clubmanager1999.backend.transaction.area.AreaTestData.ID
import com.github.clubmanager1999.backend.transaction.area.AreaTestData.NAME
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
internal class AreaControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var areaService: AreaService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnArea() {
        `when`(areaService.get(ID)).thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(get("/api/areas/$ID").withRole(MANAGE_AREAS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.name").value(NAME))
    }

    @Test
    fun shouldReturnAreas() {
        `when`(areaService.getAll())
            .thenReturn(listOf(AreaTestData.createExistingArea()))

        mockMvc
            .perform(get("/api/areas").withRole(MANAGE_AREAS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].name").value(NAME))
    }

    @Test
    fun shouldCreateArea() {
        `when`(areaService.create(AreaTestData.createNewArea()))
            .thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(
                post("/api/areas")
                    .withRole(MANAGE_AREAS)
                    .content(
                        objectMapper.writeValueAsString(AreaTestData.createExistingArea()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/areas/$ID"))
    }

    @Test
    fun shouldCreateMultipleAreas() {
        `when`(areaService.create(AreaTestData.createNewArea()))
            .thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(
                post("/api/areas")
                    .withRole(MANAGE_AREAS)
                    .content(
                        objectMapper.writeValueAsString(AreaTestData.createExistingArea()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/areas/$ID"))

        mockMvc
            .perform(
                post("/api/areas")
                    .withRole(MANAGE_AREAS)
                    .content(
                        objectMapper.writeValueAsString(AreaTestData.createExistingArea()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/areas/$ID"))
    }

    @Test
    fun shouldUpdateUser() {
        `when`(areaService.update(ID, AreaTestData.createNewArea()))
            .thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(
                put("/api/areas/$ID")
                    .withRole(MANAGE_AREAS)
                    .content(objectMapper.writeValueAsString(AreaTestData.createNewArea()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteUser() {
        mockMvc
            .perform(delete("/api/areas/$ID").withRole(MANAGE_AREAS))
            .andExpect(status().isNoContent)

        verify(areaService).delete(ID)
    }

    @Test
    fun shouldFailOnInvalidBodyPost() {
        `when`(areaService.create(AreaTestData.createNewArea()))
            .thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(
                post("/api/areas")
                    .withRole(MANAGE_AREAS)
                    .content(
                        objectMapper.writeValueAsString(AreaTestData.createEmptyNewArea()),
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
        `when`(areaService.update(ID, AreaTestData.createNewArea()))
            .thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(
                put("/api/areas/$ID")
                    .withRole(MANAGE_AREAS)
                    .content(
                        objectMapper.writeValueAsString(AreaTestData.createEmptyNewArea()),
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
        `when`(areaService.create(AreaTestData.createNewArea()))
            .thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(
                post("/api/areas")
                    .withRole(MANAGE_AREAS)
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
        `when`(areaService.update(ID, AreaTestData.createNewArea()))
            .thenReturn(AreaTestData.createExistingArea())

        mockMvc
            .perform(
                put("/api/areas/$ID")
                    .withRole(MANAGE_AREAS)
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
