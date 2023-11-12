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

import com.github.clubmanager1999.backend.transaction.reference.ExistingReference
import com.github.clubmanager1999.backend.transaction.reference.ReferenceEntity
import com.github.clubmanager1999.backend.transaction.reference.ReferenceTestData

const val ID = 49L

const val MATCHER = "Soap"

object MappingTestData {
    fun createNewMapping(): NewMapping {
        return NewMapping(
            matcher = MATCHER,
            reference = ReferenceTestData.createNewReference(),
        )
    }

    fun createExistingMapping(): ExistingMapping {
        return createExistingMapping(ReferenceTestData.createExistingReference())
    }

    fun createExistingMapping(reference: ExistingReference): ExistingMapping {
        return ExistingMapping(
            id = ID,
            matcher = MATCHER,
            reference = reference,
        )
    }

    fun createMappingEntity(): MappingEntity {
        return createMappingEntity(ReferenceTestData.createReferenceEntity())
    }

    fun createFlatMappingEntity(): MappingEntity {
        return createMappingEntity(ReferenceTestData.createFlatReferenceEntity())
    }

    fun createMappingEntity(referenceEntity: ReferenceEntity): MappingEntity {
        return MappingEntity(
            id = ID,
            matcher = MATCHER,
            reference = referenceEntity,
        )
    }
}
