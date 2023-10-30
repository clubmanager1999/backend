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
package com.github.clubmanager1999.backend.profile

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.clubmanager1999.backend.member.CITY
import com.github.clubmanager1999.backend.member.EMAIL
import com.github.clubmanager1999.backend.member.FIRST_NAME
import com.github.clubmanager1999.backend.member.LAST_NAME
import com.github.clubmanager1999.backend.member.STREET
import com.github.clubmanager1999.backend.member.STREET_NUMBER
import com.github.clubmanager1999.backend.member.USER_NAME
import com.github.clubmanager1999.backend.member.ZIP
import com.github.clubmanager1999.backend.oidc.Subject
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
import com.github.clubmanager1999.backend.security.withoutRole
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class ProfileControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var profileService: ProfileService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun shouldReturnProfile() {
        `when`(profileService.get(Subject(SUBJECT))).thenReturn(ProfileTestData.createProfile())

        mockMvc
            .perform(get("/api/profile").withoutRole())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.userName").value(USER_NAME))
            .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(LAST_NAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.address.street").value(STREET))
            .andExpect(jsonPath("$.address.streetNumber").value(STREET_NUMBER))
            .andExpect(jsonPath("$.address.city").value(CITY))
            .andExpect(jsonPath("$.address.zip").value(ZIP))
    }

    @Test
    fun shouldUpdateProfile() {
        `when`(profileService.update(Subject(SUBJECT), ProfileTestData.createProfileUpdate()))
            .thenReturn(ProfileTestData.createProfile())

        mockMvc
            .perform(
                put("/api/profile").withoutRole()
                    .content(objectMapper.writeValueAsString(ProfileTestData.createProfileUpdate()))
                    .contentType(MediaType.APPLICATION_JSON),
            )
            .andExpect(status().isNoContent)
    }
}
