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
package com.github.clubmanager1999.backend.oidc

import com.github.clubmanager1999.backend.member.EMAIL
import com.github.clubmanager1999.backend.member.USER_NAME
import com.github.clubmanager1999.backend.security.Permission

object OidcTestData {
    const val REALM = "clubmanager1999"
    const val ROLE = "CEO"
    const val CLIENT = "bac2b65d-46a1-49ab-8cb1-e3967ab78628"
    const val CLIENT_ID = "clubmanager1999-backend"

    fun createOidcUser(): OidcUser {
        return OidcUser(
            enabled = true,
            username = USER_NAME,
            email = EMAIL,
        )
    }

    fun createOidcRole(): OidcRole {
        return OidcRole(
            name = ROLE,
            permissions = listOf(Permission.MANAGE_MEMBERS),
        )
    }
}
