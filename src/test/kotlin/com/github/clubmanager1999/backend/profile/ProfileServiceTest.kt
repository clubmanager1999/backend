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

import com.github.clubmanager1999.backend.member.MemberService
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.member.MemberTestData.ID
import com.github.clubmanager1999.backend.oidc.Subject
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ProfileServiceTest {
    @Mock lateinit var memberService: MemberService

    @InjectMocks lateinit var profileService: ProfileService

    @Test
    fun shouldGetProfile() {
        val existingMemberWithRoles = MemberTestData.createExistingMemberWithRoles()

        `when`(memberService.get(Subject(SUBJECT))).thenReturn(existingMemberWithRoles)

        assertThat(profileService.get(Subject(SUBJECT))).isEqualTo(ProfileTestData.createProfile())
    }

    @Test
    fun shouldUpdateProfile() {
        val existingMemberWithRoles = MemberTestData.createExistingMemberWithRoles()
        val newMember = MemberTestData.createNewMember()
        val profileUpdate = ProfileTestData.createProfileUpdate()

        `when`(memberService.get(Subject(SUBJECT))).thenReturn(existingMemberWithRoles)

        `when`(memberService.update(ID, newMember)).thenReturn(existingMemberWithRoles)

        assertThat(profileService.update(Subject(SUBJECT), profileUpdate)).isEqualTo(ProfileTestData.createProfile())

        verify(memberService).update(ID, newMember)
    }
}
