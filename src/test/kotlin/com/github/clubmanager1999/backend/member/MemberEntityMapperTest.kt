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
package com.github.clubmanager1999.backend.member

import com.github.clubmanager1999.backend.membership.MembershipEntityMapper
import com.github.clubmanager1999.backend.membership.MembershipTestData
import com.github.clubmanager1999.backend.oidc.OidcTestData.ROLE
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MemberEntityMapperTest {
    @Mock lateinit var membershipEntityMapper: MembershipEntityMapper

    @InjectMocks lateinit var memberEntityMapper: MemberEntityMapper

    @Test
    fun shouldMapMemberEntityToExistingMember() {
        `when`(
            membershipEntityMapper.toExistingMembership(MembershipTestData.createMembershipEntity()),
        ).thenReturn(MembershipTestData.createExistingMembership())
        assertThat(memberEntityMapper.toExistingMember(listOf(ROLE), MemberTestData.createMemberEntity()))
            .isEqualTo(MemberTestData.createExistingMember())
    }

    @Test
    fun shouldMapNewMemberToMemberEntityWithId() {
        `when`(
            membershipEntityMapper.toMembershipEntity(MembershipTestData.createMembershipId()),
        ).thenReturn(MembershipTestData.createMembershipEntity())

        assertThat(
            memberEntityMapper.toMemberEntity(
                ID,
                SUBJECT,
                MemberTestData.createNewMember(),
            ),
        )
            .isEqualTo(MemberTestData.createMemberEntity())
    }
}
