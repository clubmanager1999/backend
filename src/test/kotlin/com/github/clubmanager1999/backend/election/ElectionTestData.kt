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
package com.github.clubmanager1999.backend.election

import com.github.clubmanager1999.backend.member.MemberEntity
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.role.RoleEntity
import com.github.clubmanager1999.backend.role.RoleTestData
import java.time.LocalDate

object ElectionTestData {
    const val ID = 52L

    val VALID_FROM: LocalDate = LocalDate.of(1999, 10, 15)

    val VALID_TO: LocalDate = VALID_FROM.plusDays(99)

    fun createNewElection(): NewElection {
        return NewElection(
            role = RoleTestData.createRoleId(),
            member = MemberTestData.createMemberId(),
            validFrom = VALID_FROM,
            validTo = VALID_TO,
        )
    }

    fun createExistingElection(): ExistingElection {
        return ExistingElection(
            id = ID,
            role = RoleTestData.createExistingRole(),
            member = MemberTestData.createExistingMember(),
            validFrom = VALID_FROM,
            validTo = VALID_TO,
        )
    }

    fun createElectionEntity(): ElectionEntity {
        return createElectionEntity(RoleTestData.createRoleEntity(), MemberTestData.createMemberEntity())
    }

    fun createElectionEntity(
        roleEntity: RoleEntity,
        memberEntity: MemberEntity,
    ): ElectionEntity {
        return ElectionEntity(
            id = ID,
            role = roleEntity,
            member = memberEntity,
            validFrom = VALID_FROM,
            validTo = VALID_TO,
        )
    }
}
