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

import com.github.clubmanager1999.backend.receipt.ReceiptEntity
import com.github.clubmanager1999.backend.receipt.ReceiptId
import com.github.clubmanager1999.backend.receipt.ReceiptTestData
import com.github.clubmanager1999.backend.receipt.toReceiptEntity
import com.github.clubmanager1999.backend.transaction.area.AreaEntity
import com.github.clubmanager1999.backend.transaction.area.AreaId
import com.github.clubmanager1999.backend.transaction.area.AreaTestData
import com.github.clubmanager1999.backend.transaction.area.toAreaEntity
import com.github.clubmanager1999.backend.transaction.purpose.PurposeEntity
import com.github.clubmanager1999.backend.transaction.purpose.PurposeId
import com.github.clubmanager1999.backend.transaction.purpose.PurposeTestData
import com.github.clubmanager1999.backend.transaction.purpose.toPurposeEntity
import com.github.clubmanager1999.backend.transaction.reference.ExistingReference
import com.github.clubmanager1999.backend.transaction.reference.NewReference
import com.github.clubmanager1999.backend.transaction.reference.ReferenceEntity
import com.github.clubmanager1999.backend.transaction.reference.ReferenceTestData
import java.math.BigDecimal
import java.time.LocalDate

object TransactionTestData {
    const val ID = 44L

    val BOOKING_DAY = LocalDate.of(2023, 11, 6)

    val VALUE_DAY = BOOKING_DAY.plusDays(1)

    const val NAME = "Robert Paulson"

    const val DESCRIPTION = "Soap order"

    val AMOUNT = BigDecimal("42.42")

    fun createNewTransaction(): NewTransaction {
        return createNewTransaction(
            ReferenceTestData.createNewReference(),
            ReceiptId(ReceiptTestData.ID),
            PurposeTestData.createPurposeId(),
            AreaTestData.createAreaId(),
        )
    }

    fun createNewTransaction(
        reference: NewReference?,
        receipt: ReceiptId?,
        purposeId: PurposeId?,
        areaId: AreaId?,
    ): NewTransaction {
        return NewTransaction(
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            description = DESCRIPTION,
            amount = AMOUNT,
            reference = reference,
            receipt = receipt,
            purpose = purposeId,
            area = areaId,
        )
    }

    fun createEmptyNewTransaction(): NewTransaction {
        return createEmptyNewTransaction(
            ReferenceTestData.createNewReference(),
            ReceiptId(ReceiptTestData.ID),
            PurposeTestData.createPurposeId(),
            AreaTestData.createAreaId(),
        )
    }

    fun createEmptyNewTransaction(
        reference: NewReference?,
        receipt: ReceiptId?,
        purposeId: PurposeId?,
        areaId: AreaId?,
    ): NewTransaction {
        return NewTransaction(
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = "",
            description = "",
            amount = AMOUNT,
            reference = reference,
            receipt = receipt,
            purpose = purposeId,
            area = areaId,
        )
    }

    fun createExistingTransaction(): ExistingTransaction {
        return createExistingTransaction(ReferenceTestData.createExistingReference())
    }

    fun createExistingTransaction(reference: ExistingReference?): ExistingTransaction {
        return ExistingTransaction(
            id = ID,
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            description = DESCRIPTION,
            amount = AMOUNT,
            reference = reference,
            receipt = ReceiptTestData.createExistingReceipt(),
            purpose = PurposeTestData.createExistingPurpose(),
            area = AreaTestData.createExistingArea(),
        )
    }

    fun createTransactionEntity(): TransactionEntity {
        return createTransactionEntity(
            ReferenceTestData.createReferenceEntity(),
            ReceiptTestData.createReceiptEntity(),
            PurposeTestData.createPurposeEntity(),
            AreaTestData.createAreaEntity(),
        )
    }

    fun createFlatTransactionEntity(): TransactionEntity {
        return createTransactionEntity(
            ReferenceTestData.createFlatReferenceEntity(),
            ReceiptTestData.createReceiptId().toReceiptEntity(),
            PurposeTestData.createPurposeId().toPurposeEntity(),
            AreaTestData.createAreaId().toAreaEntity(),
        )
    }

    fun createTransactionEntity(
        reference: ReferenceEntity?,
        receiptEntity: ReceiptEntity?,
        purposeEntity: PurposeEntity?,
        areaEntity: AreaEntity?,
    ): TransactionEntity {
        return TransactionEntity(
            id = ID,
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            description = DESCRIPTION,
            amount = AMOUNT,
            reference = reference,
            receipt = receiptEntity,
            purpose = purposeEntity,
            area = areaEntity,
        )
    }

    fun createTransactionImport(): TransactionImport {
        return TransactionImport(
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            description = DESCRIPTION,
            amount = AMOUNT,
        )
    }

    fun createTransactionId(): TransactionId {
        return TransactionId(ID)
    }
}
