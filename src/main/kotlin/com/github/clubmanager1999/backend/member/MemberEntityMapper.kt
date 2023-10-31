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
import org.springframework.stereotype.Service

@Service
class MemberEntityMapper(val membershipEntityMapper: MembershipEntityMapper) {
    fun toExistingMember(memberEntity: MemberEntity): ExistingMember {
        return ExistingMember(
            id = memberEntity.id!!,
            userName = memberEntity.userName,
            firstName = memberEntity.firstName,
            lastName = memberEntity.lastName,
            email = memberEntity.email,
            address = memberEntity.address,
            membership = membershipEntityMapper.toExistingMembership(memberEntity.membership),
        )
    }

    fun toMemberEntity(
        id: Long?,
        subject: String,
        newMember: NewMember,
    ): MemberEntity {
        return MemberEntity(
            id = id,
            subject = subject,
            userName = newMember.userName,
            firstName = newMember.firstName,
            lastName = newMember.lastName,
            email = newMember.email,
            address = newMember.address,
            membership = membershipEntityMapper.toMembershipEntity(newMember.membership),
        )
    }
}
