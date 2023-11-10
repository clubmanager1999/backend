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
package com.github.clubmanager1999.backend.transaction

import com.github.clubmanager1999.backend.receipt.ReceiptEntityMapper
import com.github.clubmanager1999.backend.receipt.ReceiptId
import com.github.clubmanager1999.backend.transaction.reference.NewReference
import com.github.clubmanager1999.backend.transaction.reference.ReferenceEntityMapper
import org.springframework.stereotype.Service

@Service
class TransactionEntityMapper(
    val referenceEntityMapper: ReferenceEntityMapper,
    val receiptEntityMapper: ReceiptEntityMapper,
) {
    fun toExistingTransaction(transactionEntity: TransactionEntity): ExistingTransaction {
        return ExistingTransaction(
            id = transactionEntity.id!!,
            bookingDay = transactionEntity.bookingDay,
            valueDay = transactionEntity.valueDay,
            name = transactionEntity.name,
            purpose = transactionEntity.purpose,
            amount = transactionEntity.amount,
            reference = transactionEntity.reference?.let { referenceEntityMapper.toExistingReference(it) },
            receipt = transactionEntity.receipt?.let { receiptEntityMapper.toExistingReceipt(it) },
        )
    }

    fun toTransactionEntity(
        id: Long?,
        newTransaction: NewTransaction,
    ): TransactionEntity {
        return TransactionEntity(
            id = id,
            bookingDay = newTransaction.bookingDay,
            valueDay = newTransaction.valueDay,
            name = newTransaction.name,
            purpose = newTransaction.purpose,
            amount = newTransaction.amount,
            reference = newTransaction.reference?.let { referenceEntityMapper.toReferenceEntity(it) },
            receipt = newTransaction.receipt?.let { receiptEntityMapper.toReceiptEntity(it) },
        )
    }

    fun toNewTransaction(
        transactionImport: TransactionImport,
        newReference: NewReference?,
        receiptId: ReceiptId?,
    ): NewTransaction {
        return NewTransaction(
            bookingDay = transactionImport.bookingDay,
            valueDay = transactionImport.valueDay,
            name = transactionImport.name,
            purpose = transactionImport.purpose,
            amount = transactionImport.amount,
            reference = newReference,
            receipt = receiptId,
        )
    }
}
