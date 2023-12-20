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
package com.github.clubmanager1999.backend.domain.creditor

import org.springframework.stereotype.Service

@Service
class CreditorService(
    val creditorRepository: CreditorRepository,
) {
    fun get(id: Long): ExistingCreditor {
        return creditorRepository
            .findById(id)
            .map { it.toExistingCreditor() }
            .orElseThrow { CreditorNotFoundException(id) }
    }

    fun getAll(): List<ExistingCreditor> {
        return creditorRepository.findAll().map { it.toExistingCreditor() }
    }

    fun create(newCreditor: NewCreditor): CreditorId {
        return newCreditor
            .toCreditorEntity(null)
            .let { creditorRepository.save(it) }
            .toCreditorId()
    }

    fun update(
        id: Long,
        newCreditor: NewCreditor,
    ): CreditorId {
        return creditorRepository
            .findById(id)
            .orElseThrow { CreditorNotFoundException(id) }
            .let { newCreditor.toCreditorEntity(it.id) }
            .let { creditorRepository.save(it) }
            .toCreditorId()
    }

    fun delete(id: Long) {
        creditorRepository.deleteById(id)
    }
}
