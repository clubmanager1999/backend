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

import com.github.clubmanager1999.backend.creditor.CreditorId
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReceiptService(
    val receiptRepository: ReceiptRepository,
) {
    fun get(id: Long): ExistingReceipt {
        return receiptRepository
            .findById(id)
            .map { it.toExistingReceipt() }
            .orElseThrow { ReceiptNotFoundException(id) }
    }

    fun findByCreditorAndDate(
        creditorId: CreditorId,
        date: LocalDate,
    ): ExistingReceipt? {
        return receiptRepository
            .findAllByCreditorAndDate(creditorId.id, date)
            .map { it.toExistingReceipt() }
            .firstOrNull()
    }

    fun getAll(): List<ExistingReceipt> {
        return receiptRepository.findAll().map { it.toExistingReceipt() }
    }

    fun create(newReceipt: NewReceipt): ReceiptId {
        if (
            receiptRepository.findAllByCreditorAndDate(newReceipt.creditor.id, newReceipt.validFrom).isNotEmpty() ||
            receiptRepository.findAllByCreditorAndDate(newReceipt.creditor.id, newReceipt.validTo).isNotEmpty()
        ) {
            throw OverlappingReceiptException()
        }

        return newReceipt
            .toReceiptEntity(null)
            .let { receiptRepository.save(it) }
            .toReceiptId()
    }

    fun update(
        id: Long,
        newReceipt: NewReceipt,
    ): ReceiptId {
        if (
            receiptRepository.findAllByCreditorAndDate(newReceipt.creditor.id, newReceipt.validFrom).isNotEmpty() ||
            receiptRepository.findAllByCreditorAndDate(newReceipt.creditor.id, newReceipt.validTo).isNotEmpty()
        ) {
            throw OverlappingReceiptException()
        }

        return receiptRepository
            .findById(id)
            .orElseThrow { ReceiptNotFoundException(id) }
            .let { newReceipt.toReceiptEntity(it.id) }
            .let { receiptRepository.save(it) }
            .toReceiptId()
    }

    fun delete(id: Long) {
        receiptRepository.deleteById(id)
    }
}
