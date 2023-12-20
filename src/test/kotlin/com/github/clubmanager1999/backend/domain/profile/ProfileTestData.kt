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
package com.github.clubmanager1999.backend.domain.profile

import com.github.clubmanager1999.backend.domain.member.MemberTestData.EMAIL
import com.github.clubmanager1999.backend.domain.member.MemberTestData.FIRST_NAME
import com.github.clubmanager1999.backend.domain.member.MemberTestData.LAST_NAME
import com.github.clubmanager1999.backend.domain.member.MemberTestData.USER_NAME
import com.github.clubmanager1999.backend.domain.member.MemberTestData.createAddress
import com.github.clubmanager1999.backend.domain.member.MemberTestData.createEmptyAddress
import com.github.clubmanager1999.backend.domain.membership.MembershipTestData

object ProfileTestData {
    fun createProfile(): Profile {
        return Profile(
            userName = USER_NAME,
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            email = EMAIL,
            address = createAddress(),
            membership = MembershipTestData.createExistingMembership(),
        )
    }

    fun createProfileUpdate(): ProfileUpdate {
        return ProfileUpdate(
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            email = EMAIL,
            address = createAddress(),
        )
    }

    fun createEmptyProfileUpdate(): ProfileUpdate {
        return ProfileUpdate(
            firstName = "",
            lastName = "",
            email = "",
            address = createEmptyAddress(),
        )
    }
}
