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

import com.github.clubmanager1999.backend.transaction.area.AreaEntity
import com.github.clubmanager1999.backend.transaction.purpose.PurposeEntity
import com.github.clubmanager1999.backend.transaction.reference.ReferenceEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

@Entity
data class MappingEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long?,
    val matcher: String,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    val reference: ReferenceEntity,
    @ManyToOne
    val purpose: PurposeEntity?,
    @ManyToOne
    val area: AreaEntity?,
)
