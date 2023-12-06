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
package com.github.clubmanager1999.backend.receipt

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.error.ErrorCode
import com.github.clubmanager1999.backend.receipt.ReceiptTestData.ID
import com.github.clubmanager1999.backend.receipt.ReceiptTestData.NAME
import com.github.clubmanager1999.backend.receipt.ReceiptTestData.VALID_FROM
import com.github.clubmanager1999.backend.receipt.ReceiptTestData.VALID_TO
import com.github.clubmanager1999.backend.security.Permission.MANAGE_RECEIPTS
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
internal class ReceiptControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var receiptService: ReceiptService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnReceipt() {
        `when`(receiptService.get(ID)).thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(get("/api/receipts/$ID").withRole(MANAGE_RECEIPTS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.validFrom").value(VALID_FROM.toString()))
            .andExpect(jsonPath("$.validTo").value(VALID_TO.toString()))
            .andExpect(jsonPath("$.creditor.id").value(CreditorTestData.ID))
            .andExpect(jsonPath("$.creditor.name").value(CreditorTestData.NAME))
    }

    @Test
    fun shouldReturnReceipts() {
        `when`(receiptService.getAll())
            .thenReturn(listOf(ReceiptTestData.createExistingReceipt()))

        mockMvc
            .perform(get("/api/receipts").withRole(MANAGE_RECEIPTS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].name").value(NAME))
            .andExpect(jsonPath("$[0].validFrom").value(VALID_FROM.toString()))
            .andExpect(jsonPath("$[0].validTo").value(VALID_TO.toString()))
            .andExpect(jsonPath("$[0].creditor.id").value(CreditorTestData.ID))
            .andExpect(jsonPath("$[0].creditor.name").value(CreditorTestData.NAME))
    }

    @Test
    fun shouldCreateReceipt() {
        `when`(receiptService.create(ReceiptTestData.createNewReceipt()))
            .thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(
                post("/api/receipts")
                    .withRole(MANAGE_RECEIPTS)
                    .content(
                        objectMapper.writeValueAsString(ReceiptTestData.createExistingReceipt()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/receipts/$ID"))
    }

    @Test
    fun shouldCreateMultipleReceipts() {
        `when`(receiptService.create(ReceiptTestData.createNewReceipt()))
            .thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(
                post("/api/receipts")
                    .withRole(MANAGE_RECEIPTS)
                    .content(
                        objectMapper.writeValueAsString(ReceiptTestData.createExistingReceipt()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/receipts/$ID"))

        mockMvc
            .perform(
                post("/api/receipts")
                    .withRole(MANAGE_RECEIPTS)
                    .content(
                        objectMapper.writeValueAsString(ReceiptTestData.createExistingReceipt()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/receipts/$ID"))
    }

    @Test
    fun shouldUpdateReceipt() {
        `when`(receiptService.update(ID, ReceiptTestData.createNewReceipt()))
            .thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(
                put("/api/receipts/$ID")
                    .withRole(MANAGE_RECEIPTS)
                    .content(objectMapper.writeValueAsString(ReceiptTestData.createNewReceipt()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteReceipt() {
        mockMvc
            .perform(delete("/api/receipts/$ID").withRole(MANAGE_RECEIPTS))
            .andExpect(status().isNoContent)

        verify(receiptService).delete(ID)
    }

    @Test
    fun shouldFailOnInvalidBodyPost() {
        `when`(receiptService.create(ReceiptTestData.createNewReceipt()))
            .thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(
                post("/api/receipts")
                    .withRole(MANAGE_RECEIPTS)
                    .content(
                        objectMapper.writeValueAsString(ReceiptTestData.createEmptyNewReceipt()),
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
        `when`(receiptService.update(ID, ReceiptTestData.createNewReceipt()))
            .thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(
                put("/api/receipts/$ID")
                    .withRole(MANAGE_RECEIPTS)
                    .content(
                        objectMapper.writeValueAsString(ReceiptTestData.createEmptyNewReceipt()),
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
        `when`(receiptService.create(ReceiptTestData.createNewReceipt()))
            .thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(
                post("/api/receipts")
                    .withRole(MANAGE_RECEIPTS)
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
        `when`(receiptService.update(ID, ReceiptTestData.createNewReceipt()))
            .thenReturn(ReceiptTestData.createExistingReceipt())

        mockMvc
            .perform(
                put("/api/receipts/$ID")
                    .withRole(MANAGE_RECEIPTS)
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
