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
package com.github.clubmanager1999.backend.domain.member

import com.github.clubmanager1999.backend.domain.membership.MembershipEntity
import com.github.clubmanager1999.backend.domain.membership.toExistingMembership
import com.github.clubmanager1999.backend.domain.membership.toMembershipEntity
import java.math.BigDecimal

fun MemberEntity.toExistingMember(): ExistingMember {
    return ExistingMember(
        id = this.id!!,
        userName = this.userName,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        address = this.address,
        membership = this.membership.toExistingMembership(),
    )
}

fun MemberEntity.toMemberId(): MemberId {
    return MemberId(this.id!!)
}

fun NewMember.toMemberEntity(
    id: Long?,
    subject: String,
): MemberEntity {
    return MemberEntity(
        id = id,
        subject = subject,
        userName = this.userName,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        address = this.address,
        membership = this.membership.toMembershipEntity(),
    )
}

fun MemberId.toMemberEntity(): MemberEntity {
    return MemberEntity(
        id = this.id,
        subject = "",
        userName = "",
        firstName = "",
        lastName = "",
        email = "",
        address = Address("", "", "", ""),
        membership = MembershipEntity(-1, "", BigDecimal.ZERO),
    )
}
