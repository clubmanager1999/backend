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
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.util.AssertionErrors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
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
        Group(
            permission = Permission.MANAGE_MEMBERS,
            endpoints =
                listOf(
                    Endpoint("Get member by id", HttpMethod.GET, "/api/members/42"),
                    Endpoint("Get all members", HttpMethod.GET, "/api/members"),
                    Endpoint("Add member", HttpMethod.POST, "/api/members"),
                    Endpoint("Update member", HttpMethod.PUT, "/api/members/42"),
                    Endpoint("Delete member", HttpMethod.DELETE, "/api/members/42"),
                ),
        )

    private final val membershipEndpoints =
        Group(
            permission = Permission.MANAGE_MEMBERSHIPS,
            endpoints =
                listOf(
                    Endpoint("Get membership by id", HttpMethod.GET, "/api/memberships/43"),
                    Endpoint("Get all memberships", HttpMethod.GET, "/api/memberships"),
                    Endpoint("Add membership", HttpMethod.POST, "/api/memberships"),
                    Endpoint("Update membership", HttpMethod.PUT, "/api/memberships/43"),
                    Endpoint("Delete membership", HttpMethod.DELETE, "/api/memberships/43"),
                ),
        )

    private final val roleEndpoints =
        Group(
            permission = Permission.MANAGE_ROLES,
            endpoints =
                listOf(
                    Endpoint("Get role by name", HttpMethod.GET, "/api/roles/foo"),
                    Endpoint("Get all roles", HttpMethod.GET, "/api/roles"),
                    Endpoint("Add role", HttpMethod.POST, "/api/roles"),
                    Endpoint("Update role", HttpMethod.PUT, "/api/roles/42"),
                    Endpoint("Delete role", HttpMethod.DELETE, "/api/roles/foo"),
                    Endpoint("Add permission", HttpMethod.POST, "/api/roles/foo/permissions"),
                    Endpoint("Delete permission", HttpMethod.DELETE, "/api/roles/foo/permissions/foo"),
                    Endpoint("Add role to member", HttpMethod.PUT, "/api/roles/42/holder"),
                    Endpoint("Remove role from member", HttpMethod.DELETE, "/api/roles/42/holder"),
                ),
        )

    private final val transactionEndpoints =
        Group(
            permission = Permission.MANAGE_TRANSACTIONS,
            endpoints =
                listOf(
                    Endpoint("Get transaction by id", HttpMethod.GET, "/api/transactions/44"),
                    Endpoint("Get all transactions", HttpMethod.GET, "/api/transactions"),
                    Endpoint("Add transaction", HttpMethod.POST, "/api/transactions"),
                    Endpoint("Update transaction", HttpMethod.PUT, "/api/transactions/44"),
                    Endpoint("Delete transaction", HttpMethod.DELETE, "/api/transactions/44"),
                ),
        )

    private final val donorEndpoints =
        Group(
            permission = Permission.MANAGE_DONORS,
            endpoints =
                listOf(
                    Endpoint("Get donor by id", HttpMethod.GET, "/api/donors/45"),
                    Endpoint("Get all donors", HttpMethod.GET, "/api/donors"),
                    Endpoint("Add donor", HttpMethod.POST, "/api/donors"),
                    Endpoint("Update donor", HttpMethod.PUT, "/api/donors/45"),
                    Endpoint("Delete donor", HttpMethod.DELETE, "/api/donors/45"),
                ),
        )

    private final val creditorEndpoints =
        Group(
            permission = Permission.MANAGE_CREDITORS,
            endpoints =
                listOf(
                    Endpoint("Get creditor by id", HttpMethod.GET, "/api/creditors/45"),
                    Endpoint("Get all creditors", HttpMethod.GET, "/api/creditors"),
                    Endpoint("Add creditor", HttpMethod.POST, "/api/creditors"),
                    Endpoint("Update creditor", HttpMethod.PUT, "/api/creditors/45"),
                    Endpoint("Delete creditor", HttpMethod.DELETE, "/api/creditors/45"),
                ),
        )

    private final val receiptEndpoints =
        Group(
            permission = Permission.MANAGE_RECEIPTS,
            endpoints =
                listOf(
                    Endpoint("Get receipt by id", HttpMethod.GET, "/api/receipts/45"),
                    Endpoint("Get all receipts", HttpMethod.GET, "/api/receipts"),
                    Endpoint("Add receipt", HttpMethod.POST, "/api/receipts"),
                    Endpoint("Update receipt", HttpMethod.PUT, "/api/receipts/45"),
                    Endpoint("Delete receipt", HttpMethod.DELETE, "/api/receipts/45"),
                ),
        )

    private final val mappingEndpoints =
        Group(
            permission = Permission.MANAGE_MAPPINGS,
            endpoints =
                listOf(
                    Endpoint("Get mapping by id", HttpMethod.GET, "/api/mappings/45"),
                    Endpoint("Get all mappings", HttpMethod.GET, "/api/mappings"),
                    Endpoint("Add mapping", HttpMethod.POST, "/api/mappings"),
                    Endpoint("Update mapping", HttpMethod.PUT, "/api/mappings/45"),
                    Endpoint("Delete mapping", HttpMethod.DELETE, "/api/mappings/45"),
                ),
        )

    private final val purposeEndpoints =
        Group(
            permission = Permission.MANAGE_PURPOSES,
            endpoints =
                listOf(
                    Endpoint("Get purpose by id", HttpMethod.GET, "/api/purposes/45"),
                    Endpoint("Get all purposes", HttpMethod.GET, "/api/purposes"),
                    Endpoint("Add purpose", HttpMethod.POST, "/api/purposes"),
                    Endpoint("Update purpose", HttpMethod.PUT, "/api/purposes/45"),
                    Endpoint("Delete purpose", HttpMethod.DELETE, "/api/purposes/45"),
                ),
        )

    private final val areaEndpoints =
        Group(
            permission = Permission.MANAGE_AREAS,
            endpoints =
                listOf(
                    Endpoint("Get area by id", HttpMethod.GET, "/api/areas/45"),
                    Endpoint("Get all areas", HttpMethod.GET, "/api/areas"),
                    Endpoint("Add area", HttpMethod.POST, "/api/areas"),
                    Endpoint("Update area", HttpMethod.PUT, "/api/areas/45"),
                    Endpoint("Delete area", HttpMethod.DELETE, "/api/areas/45"),
                ),
        )

    private final val electionEndpoints =
        Group(
            permission = Permission.MANAGE_ROLES,
            endpoints =
                listOf(
                    Endpoint("Get all elections", HttpMethod.GET, "/api/elections"),
                ),
        )

    private final val adminEndpoints =
        listOf(
            memberEndpoints, membershipEndpoints, roleEndpoints, transactionEndpoints, donorEndpoints,
            creditorEndpoints, receiptEndpoints, mappingEndpoints, purposeEndpoints, areaEndpoints,
            electionEndpoints,
        )

    private final val allEndpoints = adminEndpoints.flatMap { it.endpoints } + invalidEndpoints + userEndpoints

    @TestFactory
    fun shouldReturnUnauthorizedOnMissingJwt(): List<DynamicTest> {
        return allEndpoints
            .map { endpoint ->
                DynamicTest.dynamicTest(endpoint.name) {
                    mockMvc
                        .perform(MockMvcRequestBuilders.request(endpoint.method, endpoint.url))
                        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                }
            }
    }

    @TestFactory
    fun shouldReturnForbiddenOnMissingPermission(): List<DynamicTest> {
        return adminEndpoints
            .flatMap { group -> group.endpoints }
            .map { endpoint ->
                DynamicTest.dynamicTest(endpoint.name) {
                    mockMvc
                        .perform(MockMvcRequestBuilders.request(endpoint.method, endpoint.url).withoutRole())
                        .andExpect(MockMvcResultMatchers.status().isForbidden)
                }
            }
    }

    @TestFactory
    fun shouldReturnForbiddenOnWrongPermission(): List<DynamicTest> {
        return adminEndpoints
            .flatMap { group ->
                group.endpoints
                    .flatMap { endpoint ->
                        Permission.entries
                            .filter { it != group.permission }
                            .map { permission ->
                                DynamicTest.dynamicTest("${endpoint.name} ($permission)") {
                                    mockMvc
                                        .perform(MockMvcRequestBuilders.request(endpoint.method, endpoint.url).withRole(permission))
                                        .andExpect(MockMvcResultMatchers.status().isForbidden)
                                }
                            }
                    }
            }
    }

    @TestFactory
    fun shouldNotReturnForbiddenOnCorrectPermission(): List<DynamicTest> {
        return adminEndpoints.flatMap { group ->
            group.endpoints.map { endpoint ->
                DynamicTest.dynamicTest("${endpoint.name} (${group.permission}") {
                    mockMvc
                        .perform(MockMvcRequestBuilders.request(endpoint.method, endpoint.url).withRole(group.permission))
                        .andExpect { result: MvcResult ->
                            AssertionErrors.assertNotEquals(
                                "Status",
                                HttpStatus.FORBIDDEN,
                                result.response.status,
                            )
                        }
                }
            }
        }
    }

    data class Group(val permission: Permission, val endpoints: List<Endpoint>)

    data class Endpoint(val name: String, val method: HttpMethod, val url: String)
}
