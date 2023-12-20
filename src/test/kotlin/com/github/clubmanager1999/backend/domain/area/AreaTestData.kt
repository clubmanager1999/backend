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
package com.github.clubmanager1999.backend.domain.area

object AreaTestData {
    const val ID = 51L

    const val NAME = "special-purpose operation"

    fun createNewArea(): NewArea {
        return NewArea(
            name = NAME,
        )
    }

    fun createEmptyNewArea(): NewArea {
        return NewArea(
            name = "",
        )
    }

    fun createExistingArea(): ExistingArea {
        return ExistingArea(
            id = ID,
            name = NAME,
        )
    }

    fun createAreaEntity(): AreaEntity {
        return AreaEntity(
            id = ID,
            name = NAME,
        )
    }

    fun createAreaId(): AreaId {
        return AreaId(ID)
    }
}
