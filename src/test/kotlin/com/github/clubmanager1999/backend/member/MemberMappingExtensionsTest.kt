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

import com.github.clubmanager1999.backend.member.MemberTestData.ID
import com.github.clubmanager1999.backend.membership.MembershipTestData
import com.github.clubmanager1999.backend.membership.toMembershipEntity
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemberMappingExtensionsTest {
    @Test
    fun shouldMapMemberEntityToExistingMember() {
        assertThat(MemberTestData.createMemberEntity().toExistingMember())
            .isEqualTo(MemberTestData.createExistingMember())
    }

    @Test
    fun shouldMapNewMemberToMemberEntityWithId() {
        assertThat(
            MemberTestData.createNewMember().toMemberEntity(
                ID,
                SUBJECT,
            ),
        )
            .isEqualTo(MemberTestData.createMemberEntity(MembershipTestData.createMembershipId().toMembershipEntity()))
    }

    @Test
    fun shouldMapMemberIdToMemberEntity() {
        assertThat(
            MemberId(ID).toMemberEntity().id,
        )
            .isEqualTo(ID)
    }
}
