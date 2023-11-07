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
package com.github.clubmanager1999.backend.donor

import org.springframework.stereotype.Service

@Service
class DonorService(
    val donorEntityMapper: DonorEntityMapper,
    val donorRepository: DonorRepository,
) {
    fun get(id: Long): ExistingDonor {
        return donorRepository
            .findById(id)
            .map { donorEntityMapper.toExistingDonor(it) }
            .orElseThrow { DonorNotFoundException(id) }
    }

    fun getAll(): List<ExistingDonor> {
        return donorRepository.findAll().map { donorEntityMapper.toExistingDonor(it) }
    }

    fun create(newDonor: NewDonor): ExistingDonor {
        return newDonor
            .let { donorEntityMapper.toDonorEntity(null, it) }
            .let { donorRepository.save(it) }
            .let { donorEntityMapper.toExistingDonor(it) }
    }

    fun update(
        id: Long,
        newDonor: NewDonor,
    ): ExistingDonor {
        return donorRepository
            .findById(id)
            .orElseThrow { DonorNotFoundException(id) }
            .let { donorEntityMapper.toDonorEntity(id, newDonor) }
            .let { donorRepository.save(it) }
            .let { donorEntityMapper.toExistingDonor(it) }
    }

    fun delete(id: Long) {
        donorRepository.deleteById(id)
    }
}
