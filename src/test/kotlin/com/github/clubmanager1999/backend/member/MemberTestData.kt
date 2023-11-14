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

import com.github.clubmanager1999.backend.membership.MembershipEntity
import com.github.clubmanager1999.backend.membership.MembershipId
import com.github.clubmanager1999.backend.membership.MembershipTestData
import com.github.clubmanager1999.backend.membership.MembershipTestData.createExistingMembership
import com.github.clubmanager1999.backend.membership.MembershipTestData.createMembershipEntity
import com.github.clubmanager1999.backend.membership.toMembershipEntity
import com.github.clubmanager1999.backend.oidc.OidcTestData.ROLE
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT

object MemberTestData {
    const val ID = 42L

    const val USER_NAME = "tyler.durden"

    const val FIRST_NAME = "Tyler"

    const val LAST_NAME = "Durden"

    const val EMAIL = "info@paper-street-soap.co"

    const val STREET = "Paper Street"

    const val STREET_NUMBER = "537"

    const val ZIP = "19806"

    const val CITY = "Bradford"

    fun createNewMember(): NewMember {
        return NewMember(
            USER_NAME,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            createAddress(),
            MembershipId(MembershipTestData.ID),
        )
    }

    fun createExistingMember(): ExistingMember {
        return ExistingMember(
            ID,
            USER_NAME,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            createAddress(),
            createExistingMembership(),
        )
    }

    fun createExistingMemberWithRoles(): ExistingMemberWithRoles {
        return ExistingMemberWithRoles(
            ID,
            USER_NAME,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            createAddress(),
            createExistingMembership(),
            listOf(ROLE),
        )
    }

    fun createMemberEntity(): MemberEntity {
        return createMemberEntity(createMembershipEntity())
    }

    fun createFlatMemberEntity(): MemberEntity {
        return createMemberEntity(MembershipTestData.createMembershipId().toMembershipEntity())
    }

    fun createMemberEntity(membershipEntity: MembershipEntity): MemberEntity {
        return MemberEntity(
            ID,
            SUBJECT,
            USER_NAME,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            createAddress(),
            membershipEntity,
        )
    }

    fun createAddress(): Address {
        return Address(STREET, STREET_NUMBER, ZIP, CITY)
    }

    fun createMemberId(): MemberId {
        return MemberId(ID)
    }
}
