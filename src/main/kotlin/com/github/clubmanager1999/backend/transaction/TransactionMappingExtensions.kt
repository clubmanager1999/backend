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

import com.github.clubmanager1999.backend.receipt.ReceiptId
import com.github.clubmanager1999.backend.receipt.toExistingReceipt
import com.github.clubmanager1999.backend.receipt.toReceiptEntity
import com.github.clubmanager1999.backend.transaction.reference.NewReference
import com.github.clubmanager1999.backend.transaction.reference.toExistingReference
import com.github.clubmanager1999.backend.transaction.reference.toReferenceEntity

fun TransactionEntity.toExistingTransaction(): ExistingTransaction {
    return ExistingTransaction(
        id = this.id!!,
        bookingDay = this.bookingDay,
        valueDay = this.valueDay,
        name = this.name,
        description = this.description,
        amount = this.amount,
        reference = this.reference?.toExistingReference(),
        receipt = this.receipt?.toExistingReceipt(),
    )
}

fun NewTransaction.toTransactionEntity(id: Long?): TransactionEntity {
    return TransactionEntity(
        id = id,
        bookingDay = this.bookingDay,
        valueDay = this.valueDay,
        name = this.name,
        description = this.description,
        amount = this.amount,
        reference = this.reference?.toReferenceEntity(),
        receipt = this.receipt?.toReceiptEntity(),
    )
}

fun TransactionImport.toNewTransaction(
    newReference: NewReference?,
    receiptId: ReceiptId?,
): NewTransaction {
    return NewTransaction(
        bookingDay = this.bookingDay,
        valueDay = this.valueDay,
        name = this.name,
        description = this.description,
        amount = this.amount,
        reference = newReference,
        receipt = receiptId,
    )
}
