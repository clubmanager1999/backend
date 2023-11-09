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

import com.github.clubmanager1999.backend.creditor.CreditorEntityMapper
import com.github.clubmanager1999.backend.creditor.CreditorId
import com.github.clubmanager1999.backend.donor.DonorEntityMapper
import com.github.clubmanager1999.backend.donor.DonorId
import com.github.clubmanager1999.backend.member.MemberEntityMapper
import com.github.clubmanager1999.backend.member.MemberId
import com.github.clubmanager1999.backend.receipt.ReceiptEntityMapper
import com.github.clubmanager1999.backend.receipt.ReceiptId
import org.springframework.stereotype.Service

@Service
class TransactionEntityMapper(
    val memberEntityMapper: MemberEntityMapper,
    val donorEntityMapper: DonorEntityMapper,
    val creditorEntityMapper: CreditorEntityMapper,
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
            member = transactionEntity.member?.let { memberEntityMapper.toExistingMember(emptyList(), it) },
            donor = transactionEntity.donor?.let { donorEntityMapper.toExistingDonor(it) },
            creditor = transactionEntity.creditor?.let { creditorEntityMapper.toExistingCreditor(it) },
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
            member = newTransaction.member?.let { memberEntityMapper.toMemberEntity(it) },
            donor = newTransaction.donor?.let { donorEntityMapper.toDonorEntity(it) },
            creditor = newTransaction.creditor?.let { creditorEntityMapper.toCreditorEntity(it) },
            receipt = newTransaction.receipt?.let { receiptEntityMapper.toReceiptEntity(it) },
        )
    }

    fun toNewTransaction(
        transactionImport: TransactionImport,
        memberId: MemberId?,
        donorId: DonorId?,
        creditorId: CreditorId?,
        receiptId: ReceiptId?,
    ): NewTransaction {
        return NewTransaction(
            bookingDay = transactionImport.bookingDay,
            valueDay = transactionImport.valueDay,
            name = transactionImport.name,
            purpose = transactionImport.purpose,
            amount = transactionImport.amount,
            member = memberId,
            donor = donorId,
            creditor = creditorId,
            receipt = receiptId,
        )
    }
}
