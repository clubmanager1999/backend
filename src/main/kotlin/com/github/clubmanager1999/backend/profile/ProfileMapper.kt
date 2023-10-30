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

import com.github.clubmanager1999.backend.member.ExistingMember
import com.github.clubmanager1999.backend.member.NewMember
import org.springframework.stereotype.Service

@Service
class ProfileMapper {
    fun toProfile(existingMember: ExistingMember): Profile {
        return Profile(
            userName = existingMember.userName,
            firstName = existingMember.firstName,
            lastName = existingMember.lastName,
            email = existingMember.email,
            address = existingMember.address,
        )
    }

    fun toNewMember(
        userName: String,
        profileUpdate: ProfileUpdate,
    ): NewMember {
        return NewMember(
            userName = userName,
            firstName = profileUpdate.firstName,
            lastName = profileUpdate.lastName,
            email = profileUpdate.email,
            address = profileUpdate.address,
        )
    }
}
