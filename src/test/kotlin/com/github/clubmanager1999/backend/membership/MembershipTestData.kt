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

import java.math.BigDecimal

const val ID = 43L

const val NAME = "Aspirant"

val FEE = BigDecimal("13.37")

object MembershipTestData {
    fun createNewMembership(): NewMembership {
        return NewMembership(NAME, FEE)
    }

    fun createExistingMembership(): ExistingMembership {
        return ExistingMembership(ID, NAME, FEE)
    }

    fun createMembershipEntity(): MembershipEntity {
        return MembershipEntity(ID, NAME, FEE)
    }

    fun createMembershipId(): MembershipId {
        return MembershipId(ID)
    }
}
