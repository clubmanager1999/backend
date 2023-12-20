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
package com.github.clubmanager1999.backend.transaction.mapping

import org.springframework.stereotype.Service

@Service
class MappingService(
    val mappingRepository: MappingRepository,
) {
    fun get(id: Long): ExistingMapping {
        return mappingRepository
            .findById(id)
            .map { it.toExistingMapping() }
            .orElseThrow { MappingNotFoundException(id) }
    }

    fun getAll(): List<ExistingMapping> {
        return mappingRepository.findAll().map { it.toExistingMapping() }
    }

    fun create(newMapping: NewMapping): MappingId {
        return newMapping
            .toMappingEntity(null)
            .let { mappingRepository.save(it) }
            .toMappingId()
    }

    fun update(
        id: Long,
        newMapping: NewMapping,
    ): MappingId {
        return mappingRepository
            .findById(id)
            .orElseThrow { MappingNotFoundException(id) }
            .let { newMapping.toMappingEntity(it.id) }
            .let { mappingRepository.save(it) }
            .toMappingId()
    }

    fun delete(id: Long) {
        mappingRepository.deleteById(id)
    }
}
