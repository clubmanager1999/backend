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
package com.github.clubmanager1999.backend.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.domain.area.AreaNotFoundException
import com.github.clubmanager1999.backend.domain.creditor.CreditorNotFoundException
import com.github.clubmanager1999.backend.domain.donor.DonorNotFoundException
import com.github.clubmanager1999.backend.domain.mapping.MappingNotFoundException
import com.github.clubmanager1999.backend.domain.member.MemberNotFoundException
import com.github.clubmanager1999.backend.domain.member.SubjectNotFoundException
import com.github.clubmanager1999.backend.domain.membership.MembershipNotFoundException
import com.github.clubmanager1999.backend.domain.purpose.PurposeNotFoundException
import com.github.clubmanager1999.backend.domain.receipt.OverlappingReceiptException
import com.github.clubmanager1999.backend.domain.receipt.ReceiptNotFoundException
import com.github.clubmanager1999.backend.domain.transaction.TransactionNotFoundException
import com.github.clubmanager1999.backend.oidc.ClientNotFoundException
import com.github.clubmanager1999.backend.oidc.RoleNotFoundException
import com.github.clubmanager1999.backend.security.withoutRole
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.Mockito.reset
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class ExceptionHandlerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var testService: TestService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    private final val testCases =
        listOf(
            Case(
                Exception(),
                ApiError(ErrorCode.INTERNAL_ERROR, "Internal error"),
                HttpStatus.INTERNAL_SERVER_ERROR,
            ),
            Case(
                MemberNotFoundException(42),
                ApiError(ErrorCode.MEMBER_NOT_FOUND, "No member with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                SubjectNotFoundException("unknown"),
                ApiError(ErrorCode.SUBJECT_NOT_FOUND, "No member with subject unknown found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                MembershipNotFoundException(42),
                ApiError(ErrorCode.MEMBERSHIP_NOT_FOUND, "No membership with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                ClientNotFoundException("unknown"),
                ApiError(ErrorCode.CLIENT_NOT_FOUND, "No client with clientId unknown found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                RoleNotFoundException("unknown"),
                ApiError(ErrorCode.ROLE_NOT_FOUND, "No role with name unknown found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                TransactionNotFoundException(42),
                ApiError(ErrorCode.TRANSACTION_NOT_FOUND, "No transaction with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                DonorNotFoundException(42),
                ApiError(ErrorCode.DONOR_NOT_FOUND, "No donor with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                CreditorNotFoundException(42),
                ApiError(ErrorCode.CREDITOR_NOT_FOUND, "No creditor with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                ReceiptNotFoundException(42),
                ApiError(ErrorCode.RECEIPT_NOT_FOUND, "No receipt with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                MappingNotFoundException(42),
                ApiError(ErrorCode.MAPPING_NOT_FOUND, "No mapping with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                PurposeNotFoundException(42),
                ApiError(ErrorCode.PURPOSE_NOT_FOUND, "No purpose with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                AreaNotFoundException(42),
                ApiError(ErrorCode.AREA_NOT_FOUND, "No area with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
            Case(
                OverlappingReceiptException(),
                ApiError(ErrorCode.OVERLAPPING_RECEIPT, "There is already an receipt with a similar range"),
                HttpStatus.CONFLICT,
            ),
            Case(
                com.github.clubmanager1999.backend.domain.role.RoleNotFoundException(42),
                ApiError(ErrorCode.ROLE_NOT_FOUND, "No role with id 42 found"),
                HttpStatus.NOT_FOUND,
            ),
        )

    @TestFactory
    fun shouldReturnCorrectError(): List<DynamicTest> {
        return testCases.map {
            DynamicTest.dynamicTest(it.e.javaClass.simpleName) {
                reset(testService)
                `when`(testService.raise()).thenThrow(it.e)

                mockMvc
                    .perform(MockMvcRequestBuilders.get("/test").withoutRole())
                    .andExpect(MockMvcResultMatchers.status().`is`(it.httpStatus.value()))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(it.apiError.code.toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(it.apiError.message))
            }
        }
    }

    @Test
    fun shouldMapValidationErrors() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/test").withoutRole().content(
                    objectMapper.writeValueAsString(Data(42, null, NestedData("too long"))),
                )
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.BAD_REQUEST.value()))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation error"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields.number").value("must be less than or equal to 6"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields.empty").value("must not be null"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields.['nested.name']").value("size must be between 1 and 2"))
    }

    data class Case(val e: Exception, val apiError: ApiError, val httpStatus: HttpStatus)
}
