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
package com.github.clubmanager1999.backend.domain.mapping

import com.github.clubmanager1999.backend.domain.area.toAreaEntity
import com.github.clubmanager1999.backend.domain.area.toExistingArea
import com.github.clubmanager1999.backend.domain.purpose.toExistingPurpose
import com.github.clubmanager1999.backend.domain.purpose.toPurposeEntity
import com.github.clubmanager1999.backend.domain.reference.toExistingReference
import com.github.clubmanager1999.backend.domain.reference.toReferenceEntity

fun MappingEntity.toExistingMapping(): ExistingMapping {
    return ExistingMapping(
        id = this.id!!,
        matcher = this.matcher,
        reference = this.reference.toExistingReference(),
        purpose = this.purpose?.toExistingPurpose(),
        area = this.area?.toExistingArea(),
    )
}

fun MappingEntity.toMappingId(): MappingId {
    return MappingId(this.id!!)
}

fun NewMapping.toMappingEntity(id: Long?): MappingEntity {
    return MappingEntity(
        id = id,
        matcher = this.matcher,
        reference = this.reference.toReferenceEntity(),
        purpose = this.purpose?.toPurposeEntity(),
        area = this.area?.toAreaEntity(),
    )
}
