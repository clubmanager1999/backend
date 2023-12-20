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
package com.github.clubmanager1999.backend.domain.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.domain.creditor.CreditorTestData
import com.github.clubmanager1999.backend.domain.donor.DonorTestData
import com.github.clubmanager1999.backend.domain.member.MemberTestData
import com.github.clubmanager1999.backend.domain.membership.MembershipTestData
import com.github.clubmanager1999.backend.domain.purpose.PurposeTestData
import com.github.clubmanager1999.backend.domain.receipt.ReceiptTestData
import com.github.clubmanager1999.backend.domain.reference.ExistingCreditorReference
import com.github.clubmanager1999.backend.domain.reference.ExistingDonorReference
import com.github.clubmanager1999.backend.domain.reference.ExistingMemberReference
import com.github.clubmanager1999.backend.domain.transaction.TransactionTestData.AMOUNT
import com.github.clubmanager1999.backend.domain.transaction.TransactionTestData.BOOKING_DAY
import com.github.clubmanager1999.backend.domain.transaction.TransactionTestData.DESCRIPTION
import com.github.clubmanager1999.backend.domain.transaction.TransactionTestData.ID
import com.github.clubmanager1999.backend.domain.transaction.TransactionTestData.NAME
import com.github.clubmanager1999.backend.domain.transaction.TransactionTestData.VALUE_DAY
import com.github.clubmanager1999.backend.error.ErrorCode
import com.github.clubmanager1999.backend.security.Permission.MANAGE_TRANSACTIONS
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
internal class TransactionControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var transactionService: TransactionService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnCreditorTransaction() {
        `when`(
            transactionService.get(43),
        ).thenReturn(TransactionTestData.createExistingTransaction(ExistingCreditorReference(CreditorTestData.createExistingCreditor())))

        mockMvc
            .perform(get("/api/transactions/43").withRole(MANAGE_TRANSACTIONS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.bookingDay").value(BOOKING_DAY.toString()))
            .andExpect(jsonPath("$.valueDay").value(VALUE_DAY.toString()))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.description").value(DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(AMOUNT))
            .andExpect(jsonPath("$.reference.creditor.id").value(CreditorTestData.ID))
            .andExpect(jsonPath("$.reference.creditor.name").value(CreditorTestData.NAME))
            .andExpect(jsonPath("$.receipt.id").value(ReceiptTestData.ID))
            .andExpect(jsonPath("$.receipt.name").value(ReceiptTestData.NAME))
            .andExpect(jsonPath("$.receipt.validFrom").value(ReceiptTestData.VALID_FROM.toString()))
            .andExpect(jsonPath("$.receipt.validTo").value(ReceiptTestData.VALID_TO.toString()))
            .andExpect(jsonPath("$.receipt.creditor.id").value(CreditorTestData.ID))
    }

    @Test
    fun shouldReturnDonorTransaction() {
        `when`(
            transactionService.get(43),
        ).thenReturn(TransactionTestData.createExistingTransaction(ExistingDonorReference(DonorTestData.createExistingDonor())))

        mockMvc
            .perform(get("/api/transactions/43").withRole(MANAGE_TRANSACTIONS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.bookingDay").value(BOOKING_DAY.toString()))
            .andExpect(jsonPath("$.valueDay").value(VALUE_DAY.toString()))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.description").value(DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(AMOUNT))
            .andExpect(jsonPath("$.reference.donor.id").value(DonorTestData.ID))
            .andExpect(jsonPath("$.reference.donor.firstName").value(DonorTestData.FIRST_NAME))
            .andExpect(jsonPath("$.reference.donor.lastName").value(DonorTestData.LAST_NAME))
            .andExpect(jsonPath("$.receipt.id").value(ReceiptTestData.ID))
            .andExpect(jsonPath("$.receipt.name").value(ReceiptTestData.NAME))
            .andExpect(jsonPath("$.receipt.validFrom").value(ReceiptTestData.VALID_FROM.toString()))
            .andExpect(jsonPath("$.receipt.validTo").value(ReceiptTestData.VALID_TO.toString()))
            .andExpect(jsonPath("$.receipt.creditor.id").value(CreditorTestData.ID))
    }

    @Test
    fun shouldReturnMemberTransaction() {
        `when`(
            transactionService.get(43),
        ).thenReturn(TransactionTestData.createExistingTransaction(ExistingMemberReference(MemberTestData.createExistingMember())))

        mockMvc
            .perform(get("/api/transactions/43").withRole(MANAGE_TRANSACTIONS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ID))
            .andExpect(jsonPath("$.bookingDay").value(BOOKING_DAY.toString()))
            .andExpect(jsonPath("$.valueDay").value(VALUE_DAY.toString()))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.description").value(DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(AMOUNT))
            .andExpect(jsonPath("$.reference.member.id").value(MemberTestData.ID))
            .andExpect(jsonPath("$.reference.member.userName").value(MemberTestData.USER_NAME))
            .andExpect(jsonPath("$.reference.member.firstName").value(MemberTestData.FIRST_NAME))
            .andExpect(jsonPath("$.reference.member.lastName").value(MemberTestData.LAST_NAME))
            .andExpect(jsonPath("$.reference.member.email").value(MemberTestData.EMAIL))
            .andExpect(jsonPath("$.reference.member.address.street").value(MemberTestData.STREET))
            .andExpect(jsonPath("$.reference.member.address.streetNumber").value(MemberTestData.STREET_NUMBER))
            .andExpect(jsonPath("$.reference.member.address.city").value(MemberTestData.CITY))
            .andExpect(jsonPath("$.reference.member.address.zip").value(MemberTestData.ZIP))
            .andExpect(jsonPath("$.reference.member.membership.id").value(MembershipTestData.ID))
            .andExpect(jsonPath("$.reference.member.membership.name").value(MembershipTestData.NAME))
            .andExpect(jsonPath("$.reference.member.membership.fee").value(MembershipTestData.FEE))
            .andExpect(jsonPath("$.receipt.id").value(ReceiptTestData.ID))
            .andExpect(jsonPath("$.receipt.name").value(ReceiptTestData.NAME))
            .andExpect(jsonPath("$.receipt.validFrom").value(ReceiptTestData.VALID_FROM.toString()))
            .andExpect(jsonPath("$.receipt.validTo").value(ReceiptTestData.VALID_TO.toString()))
            .andExpect(jsonPath("$.receipt.creditor.id").value(CreditorTestData.ID))
            .andExpect(jsonPath("$.purpose.id").value(PurposeTestData.ID))
            .andExpect(jsonPath("$.purpose.name").value(PurposeTestData.NAME))
    }

    @Test
    fun shouldReturnTransactions() {
        `when`(transactionService.getAll())
            .thenReturn(listOf(TransactionTestData.createExistingTransaction()))

        mockMvc
            .perform(get("/api/transactions").withRole(MANAGE_TRANSACTIONS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].bookingDay").value(BOOKING_DAY.toString()))
            .andExpect(jsonPath("$[0].valueDay").value(VALUE_DAY.toString()))
            .andExpect(jsonPath("$[0].name").value(NAME))
            .andExpect(jsonPath("$[0].description").value(DESCRIPTION))
            .andExpect(jsonPath("$[0].amount").value(AMOUNT))
            .andExpect(jsonPath("$[0].reference.member.id").value(MemberTestData.ID))
            .andExpect(jsonPath("$[0].reference.member.userName").value(MemberTestData.USER_NAME))
            .andExpect(jsonPath("$[0].reference.member.firstName").value(MemberTestData.FIRST_NAME))
            .andExpect(jsonPath("$[0].reference.member.lastName").value(MemberTestData.LAST_NAME))
            .andExpect(jsonPath("$[0].reference.member.email").value(MemberTestData.EMAIL))
            .andExpect(jsonPath("$[0].reference.member.address.street").value(MemberTestData.STREET))
            .andExpect(jsonPath("$[0].reference.member.address.streetNumber").value(MemberTestData.STREET_NUMBER))
            .andExpect(jsonPath("$[0].reference.member.address.city").value(MemberTestData.CITY))
            .andExpect(jsonPath("$[0].reference.member.address.zip").value(MemberTestData.ZIP))
            .andExpect(jsonPath("$[0].reference.member.membership.id").value(MembershipTestData.ID))
            .andExpect(jsonPath("$[0].reference.member.membership.name").value(MembershipTestData.NAME))
            .andExpect(jsonPath("$[0].reference.member.membership.fee").value(MembershipTestData.FEE))
            .andExpect(jsonPath("$[0].receipt.id").value(ReceiptTestData.ID))
            .andExpect(jsonPath("$[0].receipt.name").value(ReceiptTestData.NAME))
            .andExpect(jsonPath("$[0].receipt.validFrom").value(ReceiptTestData.VALID_FROM.toString()))
            .andExpect(jsonPath("$[0].receipt.validTo").value(ReceiptTestData.VALID_TO.toString()))
            .andExpect(jsonPath("$[0].receipt.creditor.id").value(CreditorTestData.ID))
            .andExpect(jsonPath("$[0].purpose.id").value(PurposeTestData.ID))
            .andExpect(jsonPath("$[0].purpose.name").value(PurposeTestData.NAME))
    }

    @Test
    fun shouldReturnTransactionsByYear() {
        `when`(transactionService.getAllByYear(VALUE_DAY.year))
            .thenReturn(listOf(TransactionTestData.createExistingTransaction()))

        mockMvc
            .perform(get("/api/transactions?year=${VALUE_DAY.year}").withRole(MANAGE_TRANSACTIONS))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].bookingDay").value(BOOKING_DAY.toString()))
            .andExpect(jsonPath("$[0].valueDay").value(VALUE_DAY.toString()))
            .andExpect(jsonPath("$[0].name").value(NAME))
            .andExpect(jsonPath("$[0].description").value(DESCRIPTION))
            .andExpect(jsonPath("$[0].amount").value(AMOUNT))
            .andExpect(jsonPath("$[0].reference.member.id").value(MemberTestData.ID))
            .andExpect(jsonPath("$[0].reference.member.userName").value(MemberTestData.USER_NAME))
            .andExpect(jsonPath("$[0].reference.member.firstName").value(MemberTestData.FIRST_NAME))
            .andExpect(jsonPath("$[0].reference.member.lastName").value(MemberTestData.LAST_NAME))
            .andExpect(jsonPath("$[0].reference.member.email").value(MemberTestData.EMAIL))
            .andExpect(jsonPath("$[0].reference.member.address.street").value(MemberTestData.STREET))
            .andExpect(jsonPath("$[0].reference.member.address.streetNumber").value(MemberTestData.STREET_NUMBER))
            .andExpect(jsonPath("$[0].reference.member.address.city").value(MemberTestData.CITY))
            .andExpect(jsonPath("$[0].reference.member.address.zip").value(MemberTestData.ZIP))
            .andExpect(jsonPath("$[0].reference.member.membership.id").value(MembershipTestData.ID))
            .andExpect(jsonPath("$[0].reference.member.membership.name").value(MembershipTestData.NAME))
            .andExpect(jsonPath("$[0].reference.member.membership.fee").value(MembershipTestData.FEE))
            .andExpect(jsonPath("$[0].receipt.id").value(ReceiptTestData.ID))
            .andExpect(jsonPath("$[0].receipt.name").value(ReceiptTestData.NAME))
            .andExpect(jsonPath("$[0].receipt.validFrom").value(ReceiptTestData.VALID_FROM.toString()))
            .andExpect(jsonPath("$[0].receipt.validTo").value(ReceiptTestData.VALID_TO.toString()))
            .andExpect(jsonPath("$[0].receipt.creditor.id").value(CreditorTestData.ID))
            .andExpect(jsonPath("$[0].purpose.id").value(PurposeTestData.ID))
            .andExpect(jsonPath("$[0].purpose.name").value(PurposeTestData.NAME))
    }

    @Test
    fun shouldCreateTransaction() {
        `when`(transactionService.create(TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createTransactionId())

        mockMvc
            .perform(
                post("/api/transactions")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(TransactionTestData.createExistingTransaction()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/transactions/$ID"))
    }

    @Test
    fun shouldCreateMultipleTransactions() {
        `when`(transactionService.create(TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createTransactionId())

        mockMvc
            .perform(
                post("/api/transactions")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(TransactionTestData.createExistingTransaction()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/transactions/$ID"))

        mockMvc
            .perform(
                post("/api/transactions")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(TransactionTestData.createExistingTransaction()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "/api/transactions/$ID"))
    }

    @Test
    fun shouldUpdateTransaction() {
        `when`(transactionService.update(43, TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createTransactionId())

        mockMvc
            .perform(
                put("/api/transactions/$ID")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(objectMapper.writeValueAsString(TransactionTestData.createNewTransaction()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }

    @Test
    fun shouldDeleteTransaction() {
        mockMvc
            .perform(delete("/api/transactions/$ID").withRole(MANAGE_TRANSACTIONS))
            .andExpect(status().isNoContent)

        verify(transactionService).delete(ID)
    }

    @Test
    fun shouldImportTransactions() {
        mockMvc
            .perform(
                post("/api/transactions/imports")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(listOf(TransactionTestData.createTransactionImport())),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)

        verify(transactionService).import(listOf(TransactionTestData.createTransactionImport()))
    }

    @Test
    fun shouldFailOnInvalidBodyPost() {
        `when`(transactionService.create(TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createTransactionId())

        mockMvc
            .perform(
                post("/api/transactions")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(TransactionTestData.createEmptyNewTransaction()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.name").value("must not be blank"))
            .andExpect(jsonPath("$.fields.description").value("must not be blank"))
    }

    @Test
    fun shouldFailOnInvalidBodyPut() {
        `when`(transactionService.update(ID, TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createTransactionId())

        mockMvc
            .perform(
                put("/api/transactions/$ID")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(TransactionTestData.createEmptyNewTransaction()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.name").value("must not be blank"))
            .andExpect(jsonPath("$.fields.description").value("must not be blank"))
    }

    @Test
    fun shouldFailOnEmptyBodyPost() {
        `when`(transactionService.create(TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createTransactionId())

        mockMvc
            .perform(
                post("/api/transactions")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(emptyMap<Void, Void>()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.bookingDay").value("must not be null"))
    }

    @Test
    fun shouldFailOnEmptyBodyPut() {
        `when`(transactionService.update(ID, TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createTransactionId())

        mockMvc
            .perform(
                put("/api/transactions/$ID")
                    .withRole(MANAGE_TRANSACTIONS)
                    .content(
                        objectMapper.writeValueAsString(emptyMap<Void, Void>()),
                    )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.fields.bookingDay").value("must not be null"))
    }
}
