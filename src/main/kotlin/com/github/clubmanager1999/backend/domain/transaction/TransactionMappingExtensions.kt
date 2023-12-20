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
package com.github.clubmanager1999.backend.domain.transaction

import com.github.clubmanager1999.backend.domain.area.AreaId
import com.github.clubmanager1999.backend.domain.area.toAreaEntity
import com.github.clubmanager1999.backend.domain.area.toExistingArea
import com.github.clubmanager1999.backend.domain.purpose.PurposeId
import com.github.clubmanager1999.backend.domain.purpose.toExistingPurpose
import com.github.clubmanager1999.backend.domain.purpose.toPurposeEntity
import com.github.clubmanager1999.backend.domain.receipt.ReceiptId
import com.github.clubmanager1999.backend.domain.receipt.toExistingReceipt
import com.github.clubmanager1999.backend.domain.receipt.toReceiptEntity
import com.github.clubmanager1999.backend.domain.reference.NewReference
import com.github.clubmanager1999.backend.domain.reference.toExistingReference
import com.github.clubmanager1999.backend.domain.reference.toReferenceEntity

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
        purpose = this.purpose?.toExistingPurpose(),
        area = this.area?.toExistingArea(),
    )
}

fun TransactionEntity.toTransactionId(): TransactionId {
    return TransactionId(this.id!!)
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
        purpose = this.purpose?.toPurposeEntity(),
        area = this.area?.toAreaEntity(),
    )
}

fun TransactionImport.toNewTransaction(
    newReference: NewReference?,
    receiptId: ReceiptId?,
    purposeId: PurposeId?,
    areaId: AreaId?,
): NewTransaction {
    return NewTransaction(
        bookingDay = this.bookingDay,
        valueDay = this.valueDay,
        name = this.name,
        description = this.description,
        amount = this.amount,
        reference = newReference,
        receipt = receiptId,
        purpose = purposeId,
        area = areaId,
    )
}
