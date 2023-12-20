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
import com.github.clubmanager1999.backend.domain.membership.MembershipId
import com.github.clubmanager1999.backend.domain.membership.MembershipTestData
import com.github.clubmanager1999.backend.domain.membership.MembershipTestData.createExistingMembership
import com.github.clubmanager1999.backend.domain.membership.MembershipTestData.createMembershipEntity
import com.github.clubmanager1999.backend.domain.membership.toMembershipEntity
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
            userName = USER_NAME,
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            email = EMAIL,
            address = createAddress(),
            membership = MembershipId(MembershipTestData.ID),
        )
    }

    fun createEmptyNewMember(): NewMember {
        return NewMember(
            userName = "",
            firstName = "",
            lastName = "",
            email = "",
            address = createEmptyAddress(),
            membership = MembershipId(MembershipTestData.ID),
        )
    }

    fun createExistingMember(): ExistingMember {
        return ExistingMember(
            id = ID,
            userName = USER_NAME,
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            email = EMAIL,
            address = createAddress(),
            membership = createExistingMembership(),
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
            id = ID,
            subject = SUBJECT,
            userName = USER_NAME,
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            email = EMAIL,
            address = createAddress(),
            membership = membershipEntity,
        )
    }

    fun createAddress(): Address {
        return Address(
            street = STREET,
            streetNumber = STREET_NUMBER,
            zip = ZIP,
            city = CITY,
        )
    }

    fun createEmptyAddress(): Address {
        return Address(
            street = "",
            streetNumber = "",
            zip = "",
            city = "",
        )
    }

    fun createMemberId(): MemberId {
        return MemberId(ID)
    }
}
