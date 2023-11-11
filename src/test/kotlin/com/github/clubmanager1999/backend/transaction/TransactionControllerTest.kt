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
package com.github.clubmanager1999.backend.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.donor.DonorTestData
import com.github.clubmanager1999.backend.member.CITY
import com.github.clubmanager1999.backend.member.EMAIL
import com.github.clubmanager1999.backend.member.FIRST_NAME
import com.github.clubmanager1999.backend.member.LAST_NAME
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.member.STREET
import com.github.clubmanager1999.backend.member.STREET_NUMBER
import com.github.clubmanager1999.backend.member.USER_NAME
import com.github.clubmanager1999.backend.member.ZIP
import com.github.clubmanager1999.backend.oidc.OidcTestData
import com.github.clubmanager1999.backend.receipt.ReceiptTestData
import com.github.clubmanager1999.backend.security.Permission.MANAGE_TRANSACTIONS
import com.github.clubmanager1999.backend.security.withRole
import com.github.clubmanager1999.backend.transaction.TransactionTestData.AMOUNT
import com.github.clubmanager1999.backend.transaction.TransactionTestData.BOOKING_DAY
import com.github.clubmanager1999.backend.transaction.TransactionTestData.ID
import com.github.clubmanager1999.backend.transaction.TransactionTestData.NAME
import com.github.clubmanager1999.backend.transaction.TransactionTestData.PURPOSE
import com.github.clubmanager1999.backend.transaction.TransactionTestData.VALUE_DAY
import com.github.clubmanager1999.backend.transaction.reference.ExistingCreditorReference
import com.github.clubmanager1999.backend.transaction.reference.ExistingDonorReference
import com.github.clubmanager1999.backend.transaction.reference.ExistingMemberReference
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
            .andExpect(jsonPath("$.purpose").value(PURPOSE))
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
            .andExpect(jsonPath("$.purpose").value(PURPOSE))
            .andExpect(jsonPath("$.amount").value(AMOUNT))
            .andExpect(jsonPath("$.reference.donor.id").value(com.github.clubmanager1999.backend.donor.ID))
            .andExpect(jsonPath("$.reference.donor.firstName").value(com.github.clubmanager1999.backend.donor.FIRST_NAME))
            .andExpect(jsonPath("$.reference.donor.lastName").value(com.github.clubmanager1999.backend.donor.LAST_NAME))
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
            .andExpect(jsonPath("$.purpose").value(PURPOSE))
            .andExpect(jsonPath("$.amount").value(AMOUNT))
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
            .andExpect(jsonPath("$.reference.member.roles").value(OidcTestData.ROLE))
            .andExpect(jsonPath("$.receipt.id").value(ReceiptTestData.ID))
            .andExpect(jsonPath("$.receipt.name").value(ReceiptTestData.NAME))
            .andExpect(jsonPath("$.receipt.validFrom").value(ReceiptTestData.VALID_FROM.toString()))
            .andExpect(jsonPath("$.receipt.validTo").value(ReceiptTestData.VALID_TO.toString()))
            .andExpect(jsonPath("$.receipt.creditor.id").value(CreditorTestData.ID))
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
            .andExpect(jsonPath("$[0].purpose").value(PURPOSE))
            .andExpect(jsonPath("$[0].amount").value(AMOUNT))
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
            .andExpect(jsonPath("$[0].reference.member.roles[0]").value(OidcTestData.ROLE))
            .andExpect(jsonPath("$[0].receipt.id").value(ReceiptTestData.ID))
            .andExpect(jsonPath("$[0].receipt.name").value(ReceiptTestData.NAME))
            .andExpect(jsonPath("$[0].receipt.validFrom").value(ReceiptTestData.VALID_FROM.toString()))
            .andExpect(jsonPath("$[0].receipt.validTo").value(ReceiptTestData.VALID_TO.toString()))
            .andExpect(jsonPath("$[0].receipt.creditor.id").value(CreditorTestData.ID))
    }

    @Test
    fun shouldCreateTransaction() {
        `when`(transactionService.create(TransactionTestData.createNewTransaction()))
            .thenReturn(TransactionTestData.createExistingTransaction())

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
            .thenReturn(TransactionTestData.createExistingTransaction())

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
            .thenReturn(TransactionTestData.createExistingTransaction())

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
}
