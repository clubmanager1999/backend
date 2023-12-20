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
package com.github.clubmanager1999.backend.domain.purpose

object PurposeTestData {
    const val ID = 50L

    const val NAME = "war chest"

    fun createNewPurpose(): NewPurpose {
        return NewPurpose(
            name = NAME,
        )
    }

    fun createEmptyNewPurpose(): NewPurpose {
        return NewPurpose(
            name = "",
        )
    }

    fun createExistingPurpose(): ExistingPurpose {
        return ExistingPurpose(
            id = ID,
            name = NAME,
        )
    }

    fun createPurposeEntity(): PurposeEntity {
        return PurposeEntity(
            id = ID,
            name = NAME,
        )
    }

    fun createPurposeId(): PurposeId {
        return PurposeId(ID)
    }
}
