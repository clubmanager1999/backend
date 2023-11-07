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
package com.github.clubmanager1999.backend.creditor

import org.springframework.stereotype.Service

@Service
class CreditorService(
    val creditorEntityMapper: CreditorEntityMapper,
    val creditorRepository: CreditorRepository,
) {
    fun get(id: Long): ExistingCreditor {
        return creditorRepository
            .findById(id)
            .map { creditorEntityMapper.toExistingCreditor(it) }
            .orElseThrow { CreditorNotFoundException(id) }
    }

    fun getAll(): List<ExistingCreditor> {
        return creditorRepository.findAll().map { creditorEntityMapper.toExistingCreditor(it) }
    }

    fun create(newCreditor: NewCreditor): ExistingCreditor {
        return newCreditor
            .let { creditorEntityMapper.toCreditorEntity(null, it) }
            .let { creditorRepository.save(it) }
            .let { creditorEntityMapper.toExistingCreditor(it) }
    }

    fun update(
        id: Long,
        newCreditor: NewCreditor,
    ): ExistingCreditor {
        return creditorRepository
            .findById(id)
            .orElseThrow { CreditorNotFoundException(id) }
            .let { creditorEntityMapper.toCreditorEntity(id, newCreditor) }
            .let { creditorRepository.save(it) }
            .let { creditorEntityMapper.toExistingCreditor(it) }
    }

    fun delete(id: Long) {
        creditorRepository.deleteById(id)
    }
}
