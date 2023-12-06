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
package com.github.clubmanager1999.backend.donor

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.donor.DonorTestData.FIRST_NAME
import com.github.clubmanager1999.backend.donor.DonorTestData.ID
import com.github.clubmanager1999.backend.donor.DonorTestData.LAST_NAME
import com.github.clubmanager1999.backend.error.ErrorCode
import com.github.clubmanager1999.backend.member.MemberTestData.CITY
import com.github.clubmanager1999.backend.member.MemberTestData.STREET
import com.github.clubmanager1999.backend.member.MemberTestData.STREET_NUMBER
import com.github.clubmanager1999.backend.member.MemberTestData.ZIP
import com.github.clubmanager1999.backend.security.Permission
import com.github.clubmanager1999.backend.security.Permission.MANAGE_DONORS
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
internal class DonorControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var donorService: DonorService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnDonor() {
        `when`(donorService.get(ID)).thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(get("/api/donors/$ID").withRole(MANAGE_DONORS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(LAST_NAME))
            .andExpect(jsonPath("$.address.street").value(STREET))
            .andExpect(jsonPath("$.address.streetNumber").value(STREET_NUMBER))
            .andExpect(jsonPath("$.address.city").value(CITY))
            .andExpect(jsonPath("$.address.zip").value(ZIP))
    }

    @Test
    fun shouldReturnDonors() {
        `when`(donorService.getAll())
            .thenReturn(listOf(DonorTestData.createExistingDonor()))

        mockMvc
            .perform(get("/api/donors").withRole(MANAGE_DONORS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].firstName").value(FIRST_NAME))
            .andExpect(jsonPath("$[0].lastName").value(LAST_NAME))
            .andExpect(jsonPath("$[0].address.street").value(STREET))
            .andExpect(jsonPath("$[0].address.streetNumber").value(STREET_NUMBER))
            .andExpect(jsonPath("$[0].address.city").value(CITY))
            .andExpect(jsonPath("$[0].address.zip").value(ZIP))
    }

    @Test
    fun shouldCreateDonor() {
        `when`(donorService.create(DonorTestData.createNewDonor()))
            .thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(
                post("/api/donors")
                    .withRole(MANAGE_DONORS)
                    .content(
                        objectMapper.writeValueAsString(DonorTestData.createExistingDonor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/donors/$ID"))
    }

    @Test
    fun shouldCreateMultipleDonors() {
        `when`(donorService.create(DonorTestData.createNewDonor()))
            .thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(
                post("/api/donors")
                    .withRole(MANAGE_DONORS)
                    .content(
                        objectMapper.writeValueAsString(DonorTestData.createExistingDonor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/donors/$ID"))

        mockMvc
            .perform(
                post("/api/donors")
                    .withRole(MANAGE_DONORS)
                    .content(
                        objectMapper.writeValueAsString(DonorTestData.createExistingDonor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/donors/$ID"))
    }

    @Test
    fun shouldUpdateUser() {
        `when`(donorService.update(ID, DonorTestData.createNewDonor()))
            .thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(
                put("/api/donors/$ID")
                    .withRole(MANAGE_DONORS)
                    .content(objectMapper.writeValueAsString(DonorTestData.createNewDonor()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteUser() {
        mockMvc
            .perform(delete("/api/donors/$ID").withRole(MANAGE_DONORS))
            .andExpect(status().isNoContent)

        verify(donorService).delete(ID)
    }

    @Test
    fun shouldFailOnInvalidBodyPost() {
        `when`(donorService.create(DonorTestData.createNewDonor()))
            .thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(
                post("/api/donors")
                    .withRole(MANAGE_DONORS)
                    .content(
                        objectMapper.writeValueAsString(DonorTestData.createEmptyNewDonor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.firstName").value("must not be blank"))
            .andExpect(jsonPath("$.fields.lastName").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.street']").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.streetNumber']").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.zip']").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.city']").value("must not be blank"))
    }

    @Test
    fun shouldFailOnInvalidBodyPut() {
        `when`(donorService.update(ID, DonorTestData.createNewDonor()))
            .thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(
                put("/api/donors/${DonorTestData.ID}")
                    .withRole(Permission.MANAGE_DONORS)
                    .content(
                        objectMapper.writeValueAsString(DonorTestData.createEmptyNewDonor()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.firstName").value("must not be blank"))
            .andExpect(jsonPath("$.fields.lastName").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.street']").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.streetNumber']").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.zip']").value("must not be blank"))
            .andExpect(jsonPath("$.fields.['address.city']").value("must not be blank"))
    }

    @Test
    fun shouldFailOnEmptyBodyPost() {
        `when`(donorService.create(DonorTestData.createNewDonor()))
            .thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(
                post("/api/donors")
                    .withRole(MANAGE_DONORS)
                    .content(
                        objectMapper.writeValueAsString(emptyMap<Void, Void>()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.firstName").value("must not be null"))
    }

    @Test
    fun shouldFailOnEmptyBodyPut() {
        `when`(donorService.update(ID, DonorTestData.createNewDonor()))
            .thenReturn(DonorTestData.createExistingDonor())

        mockMvc
            .perform(
                put("/api/donors/$ID")
                    .withRole(Permission.MANAGE_DONORS)
                    .content(
                        objectMapper.writeValueAsString(emptyMap<Void, Void>()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.firstName").value("must not be null"))
    }
}
