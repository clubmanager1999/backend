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

import com.github.clubmanager1999.backend.member.MemberNotFoundException
import com.github.clubmanager1999.backend.member.SubjectNotFoundException
import com.github.clubmanager1999.backend.security.withoutRole
import org.junit.jupiter.api.DynamicTest
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

    data class Case(val e: Exception, val apiError: ApiError, val httpStatus: HttpStatus)
}
