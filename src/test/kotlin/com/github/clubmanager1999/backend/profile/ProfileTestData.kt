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
package com.github.clubmanager1999.backend.profile

import com.github.clubmanager1999.backend.member.EMAIL
import com.github.clubmanager1999.backend.member.FIRST_NAME
import com.github.clubmanager1999.backend.member.LAST_NAME
import com.github.clubmanager1999.backend.member.MemberTestData.createAddress
import com.github.clubmanager1999.backend.member.USER_NAME

object ProfileTestData {
    fun createProfile(): Profile {
        return Profile(
            userName = USER_NAME,
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            email = EMAIL,
            address = createAddress(),
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
}
