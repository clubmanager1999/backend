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

import org.springframework.stereotype.Service

@Service
class MemberEntityMapper {
    fun toExistingMember(memberEntity: MemberEntity): ExistingMember {
        return ExistingMember(
            id = memberEntity.id!!,
            userName = memberEntity.userName,
            firstName = memberEntity.firstName,
            lastName = memberEntity.lastName,
            email = memberEntity.email,
            address = memberEntity.address,
        )
    }

    fun toMemberEntity(existingMember: ExistingMember): MemberEntity {
        return MemberEntity(
            id = existingMember.id,
            userName = existingMember.userName,
            firstName = existingMember.firstName,
            lastName = existingMember.lastName,
            email = existingMember.email,
            address = existingMember.address,
        )
    }

    fun toMemberEntity(
        id: Long?,
        newMember: NewMember,
    ): MemberEntity {
        return MemberEntity(
            id = id,
            userName = newMember.userName,
            firstName = newMember.firstName,
            lastName = newMember.lastName,
            email = newMember.email,
            address = newMember.address,
        )
    }
}
