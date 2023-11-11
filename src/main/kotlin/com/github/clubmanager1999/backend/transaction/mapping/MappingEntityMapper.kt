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

import com.github.clubmanager1999.backend.transaction.reference.ReferenceEntityMapper
import org.springframework.stereotype.Service

@Service
class MappingEntityMapper(val referenceEntityMapper: ReferenceEntityMapper) {
    fun toExistingMapping(mappingEntity: MappingEntity): ExistingMapping {
        return ExistingMapping(
            id = mappingEntity.id!!,
            matcher = mappingEntity.matcher,
            reference = referenceEntityMapper.toExistingReference(mappingEntity.reference),
        )
    }

    fun toMappingEntity(
        id: Long?,
        newMapping: NewMapping,
    ): MappingEntity {
        return MappingEntity(
            id = id,
            matcher = newMapping.matcher,
            reference = referenceEntityMapper.toReferenceEntity(newMapping.reference),
        )
    }
}
