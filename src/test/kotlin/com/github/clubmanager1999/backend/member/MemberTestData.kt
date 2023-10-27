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

const val ID = 42L

const val USER_NAME = "tyler.durden"

const val FIRST_NAME = "Tyler"

const val LAST_NAME = "Durden"

const val EMAIL = "info@paper-street-soap.co"

const val STREET = "Paper Street"

const val STREET_NUMBER = "537"

const val ZIP = "19806"

const val CITY = "Bradford"

object MemberTestData {
    fun createNewMember(): NewMember {
        return NewMember(
            USER_NAME,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            createAddress(),
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
        )
    }

    fun createMemberEntity(): MemberEntity {
        return MemberEntity(
            ID,
            USER_NAME,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            createAddress(),
        )
    }

    private fun createAddress(): Address {
        return Address(STREET, STREET_NUMBER, ZIP, CITY)
    }
}
