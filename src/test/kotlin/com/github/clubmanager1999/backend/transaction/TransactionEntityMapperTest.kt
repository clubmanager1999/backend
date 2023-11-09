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
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.donor.DonorEntityMapper
import com.github.clubmanager1999.backend.donor.DonorTestData
import com.github.clubmanager1999.backend.member.MemberEntityMapper
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.receipt.ReceiptEntityMapper
import com.github.clubmanager1999.backend.receipt.ReceiptTestData
import com.github.clubmanager1999.backend.transaction.TransactionTestData.ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TransactionEntityMapperTest {
    @Mock lateinit var memberEntityMapper: MemberEntityMapper

    @Mock lateinit var donorEntityMapper: DonorEntityMapper

    @Mock lateinit var creditorEntityMapper: CreditorEntityMapper

    @Mock lateinit var receiptEntityMapper: ReceiptEntityMapper

    @InjectMocks lateinit var transactionEntityMapper: TransactionEntityMapper

    @Test
    fun shouldMapTransactionEntityToExistingTransaction() {
        `when`(
            memberEntityMapper.toExistingMember(emptyList(), MemberTestData.createMemberEntity()),
        ).thenReturn(MemberTestData.createExistingMember())

        `when`(
            donorEntityMapper.toExistingDonor(DonorTestData.createDonorEntity()),
        ).thenReturn(DonorTestData.createExistingDonor())

        `when`(
            creditorEntityMapper.toExistingCreditor(CreditorTestData.createCreditorEntity()),
        ).thenReturn(CreditorTestData.createExistingCreditor())

        `when`(
            receiptEntityMapper.toExistingReceipt(ReceiptTestData.createReceiptEntity()),
        ).thenReturn(ReceiptTestData.createExistingReceipt())

        assertThat(
            transactionEntityMapper.toExistingTransaction(
                TransactionTestData.createTransactionEntity(),
            ),
        )
            .isEqualTo(TransactionTestData.createExistingTransaction())
    }

    @Test
    fun shouldMapTransactionEntityToExistingTransactionWithoutOtherEntities() {
        assertThat(
            transactionEntityMapper.toExistingTransaction(
                TransactionTestData.createTransactionEntity().copy(member = null, donor = null, creditor = null, receipt = null),
            ),
        )
            .isEqualTo(
                TransactionTestData.createExistingTransaction().copy(member = null, donor = null, creditor = null, receipt = null),
            )
    }

    @Test
    fun shouldMapNewTransactionToTransactionEntityWithId() {
        `when`(
            memberEntityMapper.toMemberEntity(MemberTestData.createMemberId()),
        ).thenReturn(MemberTestData.createMemberEntity())

        `when`(
            donorEntityMapper.toDonorEntity(DonorTestData.createDonorId()),
        ).thenReturn(DonorTestData.createDonorEntity())

        `when`(
            creditorEntityMapper.toCreditorEntity(CreditorTestData.createCreditorId()),
        ).thenReturn(CreditorTestData.createCreditorEntity())

        `when`(
            receiptEntityMapper.toReceiptEntity(ReceiptTestData.createReceiptId()),
        ).thenReturn(ReceiptTestData.createReceiptEntity())

        assertThat(
            transactionEntityMapper.toTransactionEntity(ID, TransactionTestData.createNewTransaction()),
        )
            .isEqualTo(TransactionTestData.createTransactionEntity())
    }

    @Test
    fun shouldMapNewTransactionToTransactionEntityWithIdWithoutOtherEntities() {
        assertThat(
            transactionEntityMapper.toTransactionEntity(
                ID,
                TransactionTestData.createNewTransaction().copy(member = null, donor = null, creditor = null, receipt = null),
            ),
        )
            .isEqualTo(TransactionTestData.createTransactionEntity().copy(member = null, donor = null, creditor = null, receipt = null))
    }

    @Test
    fun shouldMapTransactionImportToNewTransaction() {
        assertThat(
            transactionEntityMapper.toNewTransaction(
                TransactionTestData.createTransactionImport(),
                MemberTestData.createMemberId(),
                DonorTestData.createDonorId(),
                CreditorTestData.createCreditorId(),
                ReceiptTestData.createReceiptId(),
            ),
        )
            .isEqualTo(TransactionTestData.createNewTransaction())
    }
}
