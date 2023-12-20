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
package com.github.clubmanager1999.backend.domain.election

import com.github.clubmanager1999.backend.domain.election.ElectionTestData.ID
import com.github.clubmanager1999.backend.domain.election.ElectionTestData.VALID_FROM
import com.github.clubmanager1999.backend.domain.election.ElectionTestData.VALID_TO
import com.github.clubmanager1999.backend.domain.member.MemberTestData
import com.github.clubmanager1999.backend.domain.membership.MembershipTestData
import com.github.clubmanager1999.backend.domain.role.RoleTestData
import com.github.clubmanager1999.backend.security.Permission
import com.github.clubmanager1999.backend.security.withRole
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class ElectionControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @MockBean private lateinit var electionService: ElectionService

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnElections() {
        `when`(electionService.getAll())
            .thenReturn(listOf(ElectionTestData.createExistingElection()))

        mockMvc
            .perform(get("/api/elections").withRole(Permission.MANAGE_ROLES))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(ID))
            .andExpect(jsonPath("$[0].role.id").value(RoleTestData.ID))
            .andExpect(jsonPath("$[0].role.name").value(RoleTestData.NAME))
            .andExpect(jsonPath("$[0].role.permissions[0]").value(Permission.MANAGE_MEMBERS.name))
            .andExpect(jsonPath("$[0].member.id").value(MemberTestData.ID))
            .andExpect(jsonPath("$[0].member.userName").value(MemberTestData.USER_NAME))
            .andExpect(jsonPath("$[0].member.firstName").value(MemberTestData.FIRST_NAME))
            .andExpect(jsonPath("$[0].member.lastName").value(MemberTestData.LAST_NAME))
            .andExpect(jsonPath("$[0].member.email").value(MemberTestData.EMAIL))
            .andExpect(jsonPath("$[0].member.address.street").value(MemberTestData.STREET))
            .andExpect(jsonPath("$[0].member.address.streetNumber").value(MemberTestData.STREET_NUMBER))
            .andExpect(jsonPath("$[0].member.address.city").value(MemberTestData.CITY))
            .andExpect(jsonPath("$[0].member.address.zip").value(MemberTestData.ZIP))
            .andExpect(jsonPath("$[0].member.membership.id").value(MembershipTestData.ID))
            .andExpect(jsonPath("$[0].member.membership.name").value(MembershipTestData.NAME))
            .andExpect(jsonPath("$[0].member.membership.fee").value(MembershipTestData.FEE))
            .andExpect(jsonPath("$[0].validFrom").value(VALID_FROM.toString()))
            .andExpect(jsonPath("$[0].validTo").value(VALID_TO.toString()))
    }
}
