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
package com.github.clubmanager1999.backend.role

import com.github.clubmanager1999.backend.member.MemberEntity
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.security.Permission

object RoleTestData {
    const val ID = 52L

    const val NAME = "CEO"

    fun createNewRole(): NewRole {
        return NewRole(
            name = NAME,
            permissions = setOf(Permission.MANAGE_MEMBERS),
        )
    }

    fun createEmptyNewRole(): NewRole {
        return NewRole(
            name = "",
            permissions = setOf(Permission.MANAGE_MEMBERS),
        )
    }

    fun createExistingRole(): ExistingRole {
        return ExistingRole(
            id = ID,
            name = NAME,
            permissions = setOf(Permission.MANAGE_MEMBERS),
            holder = MemberTestData.createExistingMember(),
        )
    }

    fun createRoleEntity(): RoleEntity {
        return createRoleEntity(MemberTestData.createMemberEntity())
    }

    fun createRoleEntity(memberEntity: MemberEntity): RoleEntity {
        return RoleEntity(
            id = ID,
            name = NAME,
            permissions = setOf(Permission.MANAGE_MEMBERS),
            holder = memberEntity,
        )
    }

    fun createRoleId(): RoleId {
        return RoleId(ID)
    }

    fun createNewPermission(): NewPermission {
        return NewPermission(Permission.MANAGE_MEMBERS)
    }
}
