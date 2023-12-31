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

import com.github.clubmanager1999.backend.domain.area.AreaTestData
import com.github.clubmanager1999.backend.domain.area.toAreaEntity
import com.github.clubmanager1999.backend.domain.creditor.CreditorTestData
import com.github.clubmanager1999.backend.domain.creditor.toCreditorEntity
import com.github.clubmanager1999.backend.domain.mapping.MappingService
import com.github.clubmanager1999.backend.domain.mapping.MappingTestData
import com.github.clubmanager1999.backend.domain.purpose.PurposeTestData
import com.github.clubmanager1999.backend.domain.purpose.toPurposeEntity
import com.github.clubmanager1999.backend.domain.receipt.ReceiptService
import com.github.clubmanager1999.backend.domain.receipt.ReceiptTestData
import com.github.clubmanager1999.backend.domain.receipt.toReceiptEntity
import com.github.clubmanager1999.backend.domain.reference.CreditorReferenceEntity
import com.github.clubmanager1999.backend.domain.reference.ExistingCreditorReference
import com.github.clubmanager1999.backend.domain.reference.ReferenceTestData
import com.github.clubmanager1999.backend.domain.transaction.TransactionTestData.ID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {
    @Mock lateinit var transactionRepository: TransactionRepository

    @Mock lateinit var mappingService: MappingService

    @Mock lateinit var receiptService: ReceiptService

    @InjectMocks lateinit var transactionService: TransactionService

    @Test
    fun shouldGetTransactionById() {
        val existingTransaction = TransactionTestData.createExistingTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()

        `when`(transactionRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        assertThat(transactionService.get(ID)).isEqualTo(existingTransaction)
    }

    @Test
    fun shouldThrowExceptionIfTransactionIsNotFoundById() {
        `when`(transactionRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { transactionService.get(ID) }
            .isInstanceOf(TransactionNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllTransactions() {
        val existingTransaction = TransactionTestData.createExistingTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()

        `when`(transactionRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(transactionService.getAll()).containsExactly(existingTransaction)
    }

    @Test
    fun shouldGetAllTransactionsByYear() {
        val existingTransaction = TransactionTestData.createExistingTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()

        `when`(transactionRepository.findAllByYear(TransactionTestData.VALUE_DAY.year)).thenReturn(listOf(savedEntity))

        assertThat(transactionService.getAllByYear(TransactionTestData.VALUE_DAY.year)).containsExactly(existingTransaction)
    }

    @Test
    fun shouldCreateTransaction() {
        val newTransaction = TransactionTestData.createNewTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()
        val newEntity = TransactionTestData.createFlatTransactionEntity().copy(id = null)

        `when`(transactionRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(transactionService.create(newTransaction)).isEqualTo(TransactionTestData.createTransactionId())
    }

    @Test
    fun shouldUpdateTransaction() {
        val newTransaction = TransactionTestData.createNewTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()
        val updatedEntity = TransactionTestData.createFlatTransactionEntity()

        `when`(transactionRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(transactionRepository.save(updatedEntity)).thenReturn(savedEntity)

        assertThat(transactionService.update(ID, newTransaction)).isEqualTo(TransactionTestData.createTransactionId())
    }

    @Test
    fun shouldThrowExceptionIfUpdateTransactionIsNotFound() {
        val newTransaction = TransactionTestData.createNewTransaction()
        `when`(transactionRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { transactionService.update(ID, newTransaction) }
            .isInstanceOf(TransactionNotFoundException::class.java)

        verify(transactionRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteTransaction() {
        transactionService.delete(ID)

        verify(transactionRepository).deleteById(ID)
    }

    @Test
    fun shouldImportTransactions() {
        val import = TransactionTestData.createTransactionImport()
        val transactionEntity = TransactionTestData.createTransactionEntity(null, null, null, null).copy(id = null)

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        transactionService.import(listOf(import))

        verify(transactionRepository).save(transactionEntity)
    }

    @Test
    fun shouldNotImportExistingTransactions() {
        `when`(transactionRepository.findAll()).thenReturn(listOf(TransactionTestData.createTransactionEntity()))

        transactionService.import(listOf(TransactionTestData.createTransactionImport()))

        verify(transactionRepository, never()).save(any())
    }

    @Test
    fun shouldImportTransactionsWithoutMatch() {
        val mapping = MappingTestData.createExistingMapping().copy(matcher = "not match")
        val import = TransactionTestData.createTransactionImport()
        val transactionEntity = TransactionTestData.createTransactionEntity(null, null, null, null).copy(id = null)

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(mappingService.getAll()).thenReturn(listOf(mapping))

        transactionService.import(listOf(import))

        verify(transactionRepository).save(transactionEntity)
    }

    @Test
    fun shouldImportTransactionsWithMinimalMapping() {
        val mapping = MappingTestData.createExistingMapping(ReferenceTestData.createExistingReference(), null, null).copy()
        val referenceEntity = ReferenceTestData.createFlatReferenceEntity()
        val import = TransactionTestData.createTransactionImport()
        val transactionEntity = TransactionTestData.createTransactionEntity(referenceEntity, null, null, null).copy(id = null)

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(mappingService.getAll()).thenReturn(listOf(mapping))

        transactionService.import(listOf(import))

        verify(transactionRepository).save(transactionEntity)
    }

    @Test
    fun shouldImportTransactionsWithFullMapping() {
        val mapping = MappingTestData.createExistingMapping()
        val referenceEntity = ReferenceTestData.createFlatReferenceEntity()
        val purposeEntity = PurposeTestData.createPurposeId().toPurposeEntity()
        val areaEntity = AreaTestData.createAreaId().toAreaEntity()
        val import = TransactionTestData.createTransactionImport()
        val transactionEntity =
            TransactionTestData.createTransactionEntity(
                referenceEntity,
                null,
                purposeEntity,
                areaEntity,
            ).copy(id = null)

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(mappingService.getAll()).thenReturn(listOf(mapping))

        transactionService.import(listOf(import))

        verify(transactionRepository).save(transactionEntity)
    }

    @Test
    fun shouldImportTransactionsWithFullMappingAndReceipt() {
        val mapping =
            MappingTestData.createExistingMapping(
                reference = ExistingCreditorReference(creditor = CreditorTestData.createExistingCreditor()),
                purpose = PurposeTestData.createExistingPurpose(),
                area = AreaTestData.createExistingArea(),
            )
        val referenceEntity = CreditorReferenceEntity(id = null, creditor = CreditorTestData.createCreditorId().toCreditorEntity())
        val receiptEntity = ReceiptTestData.createReceiptId().toReceiptEntity()
        val purposeEntity = PurposeTestData.createPurposeId().toPurposeEntity()
        val areaEntity = AreaTestData.createAreaId().toAreaEntity()
        val import = TransactionTestData.createTransactionImport()
        val transactionEntity =
            TransactionTestData.createTransactionEntity(
                referenceEntity,
                receiptEntity,
                purposeEntity,
                areaEntity,
            ).copy(id = null)

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(mappingService.getAll()).thenReturn(listOf(mapping))

        `when`(
            receiptService.findByCreditorAndDate(CreditorTestData.createCreditorId(), TransactionTestData.VALUE_DAY),
        ).thenReturn(ReceiptTestData.createExistingReceipt())

        transactionService.import(listOf(import))

        verify(transactionRepository).save(transactionEntity)
    }
}
