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
package com.github.clubmanager1999.backend.donor

import com.github.clubmanager1999.backend.member.MemberTestData.createAddress

object DonorTestData {
    const val ID = 45L

    const val FIRST_NAME = "Richard"

    const val LAST_NAME = "Chesler"

    fun createNewDonor(): NewDonor {
        return NewDonor(
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            address = createAddress(),
        )
    }

    fun createExistingDonor(): ExistingDonor {
        return ExistingDonor(
            id = ID,
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            address = createAddress(),
        )
    }

    fun createDonorEntity(): DonorEntity {
        return DonorEntity(
            id = ID,
            firstName = FIRST_NAME,
            lastName = LAST_NAME,
            address = createAddress(),
        )
    }

    fun createFlatDonorEntity(): DonorEntity {
        return DonorEntity(
            id = ID,
            firstName = "",
            lastName = "",
            address = createAddress(),
        )
    }

    fun createDonorId(): DonorId {
        return DonorId(ID)
    }
}
