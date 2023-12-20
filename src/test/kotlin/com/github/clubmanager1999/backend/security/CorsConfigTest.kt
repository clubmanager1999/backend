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
package com.github.clubmanager1999.backend.security

import com.github.clubmanager1999.backend.domain.member.MemberService
import com.github.clubmanager1999.backend.domain.member.MemberTestData
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @MockBean
    private lateinit var memberService: MemberService

    @TestFactory
    fun shouldReturnCorsHeaders(): List<DynamicTest> {
        val origin = "http://foo.bar"

        return listOf(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
            .map { method ->
                DynamicTest.dynamicTest("Should return cors header for $method requests") {
                    mockMvc
                        .perform(
                            MockMvcRequestBuilders.request(method, "/cors")
                                .withRole(Permission.MANAGE_MEMBERS)
                                .header("Access-Control-Request-Method", method.name())
                                .header("Origin", origin),
                        )
                        .andExpect(MockMvcResultMatchers.status().isOk)
                        .andExpect(MockMvcResultMatchers.header().string("Access-Control-Allow-Origin", origin))
                }
            }
    }

    @Test
    fun shouldRejectInvalidCorsOrigin() {
        val origin = "http://foo.bar:666"

        `when`(memberService.get(42)).thenReturn(MemberTestData.createExistingMember())

        mockMvc
            .perform(
                MockMvcRequestBuilders.request(HttpMethod.GET, "/api/members/42")
                    .withRole(Permission.MANAGE_MEMBERS)
                    .header("Access-Control-Request-Method", "GET")
                    .header("Origin", origin),
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }
}
