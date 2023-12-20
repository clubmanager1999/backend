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
package com.github.clubmanager1999.backend.domain.donor

import org.springframework.stereotype.Service

@Service
class DonorService(
    val donorRepository: DonorRepository,
) {
    fun get(id: Long): ExistingDonor {
        return donorRepository
            .findById(id)
            .map { it.toExistingDonor() }
            .orElseThrow { DonorNotFoundException(id) }
    }

    fun getAll(): List<ExistingDonor> {
        return donorRepository.findAll().map { it.toExistingDonor() }
    }

    fun create(newDonor: NewDonor): DonorId {
        return newDonor
            .toDonorEntity(null)
            .let { donorRepository.save(it) }
            .toDonorId()
    }

    fun update(
        id: Long,
        newDonor: NewDonor,
    ): DonorId {
        return donorRepository
            .findById(id)
            .orElseThrow { DonorNotFoundException(id) }
            .let { newDonor.toDonorEntity(it.id) }
            .let { donorRepository.save(it) }
            .toDonorId()
    }

    fun delete(id: Long) {
        donorRepository.deleteById(id)
    }
}
