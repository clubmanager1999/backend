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
package com.github.clubmanager1999.backend.membership

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class MembershipEntityMapper {
    fun toExistingMembership(membershipEntity: MembershipEntity): ExistingMembership {
        return ExistingMembership(id = membershipEntity.id!!, name = membershipEntity.name, fee = membershipEntity.fee)
    }

    fun toMembershipEntity(existingMembership: ExistingMembership): MembershipEntity {
        return MembershipEntity(id = existingMembership.id, name = existingMembership.name, fee = existingMembership.fee)
    }

    fun toMembershipEntity(
        id: Long?,
        newMembership: NewMembership,
    ): MembershipEntity {
        return MembershipEntity(id = id, name = newMembership.name, fee = newMembership.fee)
    }

    fun toMembershipEntity(membershipId: MembershipId): MembershipEntity {
        return MembershipEntity(id = membershipId.id, name = "", fee = BigDecimal.ZERO)
    }
}
