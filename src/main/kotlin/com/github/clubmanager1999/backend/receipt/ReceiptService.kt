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
package com.github.clubmanager1999.backend.receipt

import org.springframework.stereotype.Service

@Service
class ReceiptService(
    val receiptEntityMapper: ReceiptEntityMapper,
    val receiptRepository: ReceiptRepository,
) {
    fun get(id: Long): ExistingReceipt {
        return receiptRepository
            .findById(id)
            .map { receiptEntityMapper.toExistingReceipt(it) }
            .orElseThrow { ReceiptNotFoundException(id) }
    }

    fun getAll(): List<ExistingReceipt> {
        return receiptRepository.findAll().map { receiptEntityMapper.toExistingReceipt(it) }
    }

    fun create(newReceipt: NewReceipt): ExistingReceipt {
        return newReceipt
            .let { receiptEntityMapper.toReceiptEntity(null, it) }
            .let { receiptRepository.save(it) }
            .let { receiptEntityMapper.toExistingReceipt(it) }
    }

    fun update(
        id: Long,
        newReceipt: NewReceipt,
    ): ExistingReceipt {
        return receiptRepository
            .findById(id)
            .orElseThrow { ReceiptNotFoundException(id) }
            .let { receiptEntityMapper.toReceiptEntity(id, newReceipt) }
            .let { receiptRepository.save(it) }
            .let { receiptEntityMapper.toExistingReceipt(it) }
    }

    fun delete(id: Long) {
        receiptRepository.deleteById(id)
    }
}
