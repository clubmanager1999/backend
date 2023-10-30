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

import com.github.clubmanager1999.backend.member.MemberService
import com.github.clubmanager1999.backend.oidc.Subject
import org.springframework.stereotype.Service

@Service
class ProfileService(
    val profileMapper: ProfileMapper,
    val memberService: MemberService,
) {
    fun get(subject: Subject): Profile {
        return memberService
            .get(subject)
            .let { profileMapper.toProfile(it) }
    }

    fun update(
        subject: Subject,
        profileUpdate: ProfileUpdate,
    ): Profile {
        return memberService
            .get(subject)
            .let { Pair(it.id, profileMapper.toNewMember(it.userName, profileUpdate)) }
            .let { memberService.update(it.first, it.second) }
            .let { profileMapper.toProfile(it) }
    }
}
