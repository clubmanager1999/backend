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

import com.github.clubmanager1999.backend.creditor.CreditorEntity
import com.github.clubmanager1999.backend.creditor.CreditorId
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.donor.DonorEntity
import com.github.clubmanager1999.backend.donor.DonorId
import com.github.clubmanager1999.backend.donor.DonorTestData
import com.github.clubmanager1999.backend.member.MemberEntity
import com.github.clubmanager1999.backend.member.MemberId
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.receipt.ReceiptEntity
import com.github.clubmanager1999.backend.receipt.ReceiptId
import com.github.clubmanager1999.backend.receipt.ReceiptTestData
import java.math.BigDecimal
import java.time.LocalDate

object TransactionTestData {
    const val ID = 44L

    val BOOKING_DAY = LocalDate.of(2023, 11, 6)

    val VALUE_DAY = BOOKING_DAY.plusDays(1)

    const val NAME = "Robert Paulson"

    const val PURPOSE = "Soap order"

    val AMOUNT = BigDecimal("42.42")

    fun createNewTransaction(): NewTransaction {
        return NewTransaction(
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
            member = MemberId(com.github.clubmanager1999.backend.member.ID),
            donor = DonorId(com.github.clubmanager1999.backend.donor.ID),
            creditor = CreditorId(CreditorTestData.ID),
            receipt = ReceiptId(ReceiptTestData.ID),
        )
    }

    fun createExistingTransaction(): ExistingTransaction {
        return ExistingTransaction(
            id = ID,
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
            member = MemberTestData.createExistingMember(),
            donor = DonorTestData.createExistingDonor(),
            creditor = CreditorTestData.createExistingCreditor(),
            receipt = ReceiptTestData.createExistingReceipt(),
        )
    }

    fun createTransactionEntity(): TransactionEntity {
        return createTransactionEntity(
            MemberTestData.createMemberEntity(),
            DonorTestData.createDonorEntity(),
            CreditorTestData.createCreditorEntity(),
            ReceiptTestData.createReceiptEntity(),
        )
    }

    fun createTransactionEntity(
        memberEntity: MemberEntity,
        donorEntity: DonorEntity,
        creditorEntity: CreditorEntity,
        receiptEntity: ReceiptEntity,
    ): TransactionEntity {
        return TransactionEntity(
            id = ID,
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
            member = memberEntity,
            donor = donorEntity,
            creditor = creditorEntity,
            receipt = receiptEntity,
        )
    }

    fun createTransactionImport(): TransactionImport {
        return TransactionImport(
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
        )
    }
}
