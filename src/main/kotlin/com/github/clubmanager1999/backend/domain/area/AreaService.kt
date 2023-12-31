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

import org.springframework.stereotype.Service

@Service
class AreaService(
    val areaRepository: AreaRepository,
) {
    fun get(id: Long): ExistingArea {
        return areaRepository
            .findById(id)
            .map { it.toExistingArea() }
            .orElseThrow { AreaNotFoundException(id) }
    }

    fun getAll(): List<ExistingArea> {
        return areaRepository.findAll().map { it.toExistingArea() }
    }

    fun create(newArea: NewArea): AreaId {
        return newArea
            .toAreaEntity(null)
            .let { areaRepository.save(it) }
            .toAreaId()
    }

    fun update(
        id: Long,
        newArea: NewArea,
    ): AreaId {
        return areaRepository
            .findById(id)
            .orElseThrow { AreaNotFoundException(id) }
            .let { newArea.toAreaEntity(id) }
            .let { areaRepository.save(it) }
            .toAreaId()
    }

    fun delete(id: Long) {
        areaRepository.deleteById(id)
    }
}
