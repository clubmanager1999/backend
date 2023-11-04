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

import com.github.clubmanager1999.backend.member.MemberService
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
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
class WebSecurityConfigTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @MockBean private lateinit var memberService: MemberService

    private final val invalidEndpoints =
        listOf(
            Endpoint("Get invalid endpoint", HttpMethod.GET, "/api/invalid"),
        )

    private final val userEndpoints =
        listOf(
            Endpoint("Get profile", HttpMethod.GET, "/api/profile"),
            Endpoint("Update profile", HttpMethod.PUT, "/api/profile"),
        )

    private final val memberEndpoints =
        listOf(
            Endpoint("Get member by id", HttpMethod.GET, "/api/members/42"),
            Endpoint("Get all members", HttpMethod.GET, "/api/members"),
            Endpoint("Add member", HttpMethod.POST, "/api/members"),
            Endpoint("Update member", HttpMethod.PUT, "/api/members/42"),
            Endpoint("Delete member", HttpMethod.DELETE, "/api/members/42"),
        )

    private final val membershipEndpoints =
        listOf(
            Endpoint("Get membership by id", HttpMethod.GET, "/api/memberships/43"),
            Endpoint("Get all memberships", HttpMethod.GET, "/api/memberships"),
            Endpoint("Add membership", HttpMethod.POST, "/api/memberships"),
            Endpoint("Update membership", HttpMethod.PUT, "/api/memberships/43"),
            Endpoint("Delete membership", HttpMethod.DELETE, "/api/memberships/43"),
        )

    private final val roleEndpoints =
        listOf(
            Endpoint("Get role by name", HttpMethod.GET, "/api/roles/foo"),
            Endpoint("Get all roles", HttpMethod.GET, "/api/roles"),
            Endpoint("Add role", HttpMethod.POST, "/api/roles"),
            Endpoint("Add permission", HttpMethod.POST, "/api/roles/foo/permissions"),
            Endpoint("Delete permission", HttpMethod.DELETE, "/api/roles/foo/permissions/foo"),
            Endpoint("Delete role", HttpMethod.DELETE, "/api/roles/foo"),
            Endpoint("Add role to member", HttpMethod.POST, "/api/members/42/roles"),
            Endpoint("Remove role from member", HttpMethod.DELETE, "/api/members/42/roles/foo"),
        )

    private final val adminEndpoints = memberEndpoints + membershipEndpoints + roleEndpoints

    private final val allEndpoints = invalidEndpoints + userEndpoints + adminEndpoints

    @TestFactory
    fun shouldReturnUnauthorizedOnMissingToken(): List<DynamicTest> {
        return allEndpoints.map {
            DynamicTest.dynamicTest(it.name) {
                mockMvc
                    .perform(MockMvcRequestBuilders.request(it.method, it.url))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            }
        }
    }

    @TestFactory
    fun shouldReturnForbiddenOnMissingRole(): List<DynamicTest> {
        return adminEndpoints.map {
            DynamicTest.dynamicTest(it.name) {
                mockMvc
                    .perform(MockMvcRequestBuilders.request(it.method, it.url).withoutRole())
                    .andExpect(MockMvcResultMatchers.status().isForbidden)
            }
        }
    }

    data class Endpoint(val name: String, val method: HttpMethod, val url: String)
}
