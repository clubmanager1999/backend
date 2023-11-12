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
package com.github.clubmanager1999.backend.transaction.purpose

import org.springframework.stereotype.Service

@Service
class PurposeService(
    val purposeEntityMapper: PurposeEntityMapper,
    val purposeRepository: PurposeRepository,
) {
    fun get(id: Long): ExistingPurpose {
        return purposeRepository
            .findById(id)
            .map { purposeEntityMapper.toExistingPurpose(it) }
            .orElseThrow { PurposeNotFoundException(id) }
    }

    fun getAll(): List<ExistingPurpose> {
        return purposeRepository.findAll().map { purposeEntityMapper.toExistingPurpose(it) }
    }

    fun create(newPurpose: NewPurpose): ExistingPurpose {
        return newPurpose
            .let { purposeEntityMapper.toPurposeEntity(null, it) }
            .let { purposeRepository.save(it) }
            .let { purposeEntityMapper.toExistingPurpose(it) }
    }

    fun update(
        id: Long,
        newPurpose: NewPurpose,
    ): ExistingPurpose {
        return purposeRepository
            .findById(id)
            .orElseThrow { PurposeNotFoundException(id) }
            .let { purposeEntityMapper.toPurposeEntity(id, newPurpose) }
            .let { purposeRepository.save(it) }
            .let { purposeEntityMapper.toExistingPurpose(it) }
    }

    fun delete(id: Long) {
        purposeRepository.deleteById(id)
    }
}
