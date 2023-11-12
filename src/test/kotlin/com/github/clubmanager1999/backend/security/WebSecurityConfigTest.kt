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

    private final val transactionEndpoints =
        listOf(
            Endpoint("Get transaction by id", HttpMethod.GET, "/api/transactions/44"),
            Endpoint("Get all transactions", HttpMethod.GET, "/api/transactions"),
            Endpoint("Add transaction", HttpMethod.POST, "/api/transactions"),
            Endpoint("Update transaction", HttpMethod.PUT, "/api/transactions/44"),
            Endpoint("Delete transaction", HttpMethod.DELETE, "/api/transactions/44"),
        )

    private final val donorEndpoints =
        listOf(
            Endpoint("Get donor by id", HttpMethod.GET, "/api/donors/45"),
            Endpoint("Get all donors", HttpMethod.GET, "/api/donors"),
            Endpoint("Add donor", HttpMethod.POST, "/api/donors"),
            Endpoint("Update donor", HttpMethod.PUT, "/api/donors/45"),
            Endpoint("Delete donor", HttpMethod.DELETE, "/api/donors/45"),
        )

    private final val creditorEndpoints =
        listOf(
            Endpoint("Get creditor by id", HttpMethod.GET, "/api/creditors/45"),
            Endpoint("Get all creditors", HttpMethod.GET, "/api/creditors"),
            Endpoint("Add creditor", HttpMethod.POST, "/api/creditors"),
            Endpoint("Update creditor", HttpMethod.PUT, "/api/creditors/45"),
            Endpoint("Delete creditor", HttpMethod.DELETE, "/api/creditors/45"),
        )

    private final val receiptEndpoints =
        listOf(
            Endpoint("Get receipt by id", HttpMethod.GET, "/api/receipts/45"),
            Endpoint("Get all receipts", HttpMethod.GET, "/api/receipts"),
            Endpoint("Add receipt", HttpMethod.POST, "/api/receipts"),
            Endpoint("Update receipt", HttpMethod.PUT, "/api/receipts/45"),
            Endpoint("Delete receipt", HttpMethod.DELETE, "/api/receipts/45"),
        )

    private final val mappingEndpoints =
        listOf(
            Endpoint("Get mapping by id", HttpMethod.GET, "/api/mappings/45"),
            Endpoint("Get all mappings", HttpMethod.GET, "/api/mappings"),
            Endpoint("Add mapping", HttpMethod.POST, "/api/mappings"),
            Endpoint("Update mapping", HttpMethod.PUT, "/api/mappings/45"),
            Endpoint("Delete mapping", HttpMethod.DELETE, "/api/mappings/45"),
        )

    private final val purposeEndpoints =
        listOf(
            Endpoint("Get purpose by id", HttpMethod.GET, "/api/purposes/45"),
            Endpoint("Get all purposes", HttpMethod.GET, "/api/purposes"),
            Endpoint("Add purpose", HttpMethod.POST, "/api/purposes"),
            Endpoint("Update purpose", HttpMethod.PUT, "/api/purposes/45"),
            Endpoint("Delete purpose", HttpMethod.DELETE, "/api/purposes/45"),
        )

    private final val adminEndpoints =
        memberEndpoints + membershipEndpoints + roleEndpoints + transactionEndpoints + donorEndpoints +
            creditorEndpoints + receiptEndpoints + mappingEndpoints + purposeEndpoints

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
