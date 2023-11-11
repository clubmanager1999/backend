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

import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.receipt.ReceiptService
import com.github.clubmanager1999.backend.receipt.ReceiptTestData
import com.github.clubmanager1999.backend.transaction.mapping.MappingService
import com.github.clubmanager1999.backend.transaction.mapping.MappingTestData
import com.github.clubmanager1999.backend.transaction.reference.CreditorReferenceEntity
import com.github.clubmanager1999.backend.transaction.reference.ExistingCreditorReference
import com.github.clubmanager1999.backend.transaction.reference.NewCreditorReference
import com.github.clubmanager1999.backend.transaction.reference.ReferenceEntityMapper
import com.github.clubmanager1999.backend.transaction.reference.ReferenceTestData
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
    @Mock lateinit var transactionEntityMapper: TransactionEntityMapper

    @Mock lateinit var transactionRepository: TransactionRepository

    @Mock lateinit var mappingService: MappingService

    @Mock lateinit var referenceEntityMapper: ReferenceEntityMapper

    @Mock lateinit var receiptService: ReceiptService

    @InjectMocks lateinit var transactionService: TransactionService

    @Test
    fun shouldGetTransactionById() {
        val existingTransaction = TransactionTestData.createExistingTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()

        `when`(transactionEntityMapper.toExistingTransaction(savedEntity)).thenReturn(existingTransaction)

        `when`(transactionRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(transactionService.get(42)).isEqualTo(existingTransaction)
    }

    @Test
    fun shouldThrowExceptionIfTransactionIsNotFoundById() {
        `when`(transactionRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { transactionService.get(42) }
            .isInstanceOf(TransactionNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllTransactions() {
        val existingTransaction = TransactionTestData.createExistingTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()

        `when`(transactionRepository.findAll()).thenReturn(listOf(savedEntity))

        `when`(transactionEntityMapper.toExistingTransaction(savedEntity)).thenReturn(existingTransaction)

        assertThat(transactionService.getAll()).containsExactly(existingTransaction)
    }

    @Test
    fun shouldCreateTransaction() {
        val newTransaction = TransactionTestData.createNewTransaction()
        val existingTransaction = TransactionTestData.createExistingTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(transactionEntityMapper.toTransactionEntity(null, newTransaction)).thenReturn(newEntity)

        `when`(transactionRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(transactionEntityMapper.toExistingTransaction(savedEntity)).thenReturn(existingTransaction)

        assertThat(transactionService.create(newTransaction)).isEqualTo(existingTransaction)
    }

    @Test
    fun shouldUpdateTransaction() {
        val newTransaction = TransactionTestData.createNewTransaction()
        val existingTransaction = TransactionTestData.createExistingTransaction()
        val savedEntity = TransactionTestData.createTransactionEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(transactionRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(transactionEntityMapper.toTransactionEntity(42, newTransaction)).thenReturn(newEntity)

        `when`(transactionRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(transactionEntityMapper.toExistingTransaction(savedEntity)).thenReturn(existingTransaction)

        assertThat(transactionService.update(42, newTransaction)).isEqualTo(existingTransaction)
    }

    @Test
    fun shouldThrowExceptionIfUpdateTransactionIsNotFound() {
        val newTransaction = TransactionTestData.createNewTransaction()
        `when`(transactionRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { transactionService.update(42, newTransaction) }
            .isInstanceOf(TransactionNotFoundException::class.java)

        verify(transactionRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteTransaction() {
        transactionService.delete(42)

        verify(transactionRepository).deleteById(42)
    }

    @Test
    fun shouldImportTransactions() {
        val import = TransactionTestData.createTransactionImport()
        val newTransaction = TransactionTestData.createNewTransaction(null, null)
        val transactionEntity = TransactionTestData.createTransactionEntity(null, null)

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(
            transactionEntityMapper.toNewTransaction(import, null, null),
        ).thenReturn(newTransaction)

        `when`(
            transactionEntityMapper.toTransactionEntity(null, newTransaction),
        ).thenReturn(transactionEntity)

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
    fun shouldImportTransactionsWithCreditor() {
        val mapping = MappingTestData.createExistingMapping()
        val newReference = ReferenceTestData.createNewReference()
        val referenceEntity = ReferenceTestData.createReferenceEntity()
        val import = TransactionTestData.createTransactionImport()
        val newTransaction = TransactionTestData.createNewTransaction(newReference, null)
        val transactionEntity = TransactionTestData.createTransactionEntity(referenceEntity, null)

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(mappingService.getAll()).thenReturn(listOf(mapping))

        `when`(
            referenceEntityMapper.toNewReference(mapping.reference),
        ).thenReturn(newReference)

        `when`(
            transactionEntityMapper.toNewTransaction(import, newReference, null),
        ).thenReturn(newTransaction)

        `when`(
            transactionEntityMapper.toTransactionEntity(null, newTransaction),
        ).thenReturn(transactionEntity)

        transactionService.import(listOf(import))

        verify(transactionRepository).save(transactionEntity)
    }

    @Test
    fun shouldImportTransactionsWithCreditorAndReceipt() {
        val mapping = MappingTestData.createExistingMapping(ExistingCreditorReference(creditor = CreditorTestData.createExistingCreditor()))
        val newReference = NewCreditorReference(creditor = CreditorTestData.createCreditorId())
        val referenceEntity = CreditorReferenceEntity(id = null, creditor = CreditorTestData.createCreditorEntity())
        val import = TransactionTestData.createTransactionImport()
        val newTransaction = TransactionTestData.createNewTransaction(newReference, ReceiptTestData.createReceiptId())
        val transactionEntity = TransactionTestData.createTransactionEntity(referenceEntity, ReceiptTestData.createReceiptEntity())

        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(mappingService.getAll()).thenReturn(listOf(mapping))

        `when`(
            referenceEntityMapper.toNewReference(mapping.reference),
        ).thenReturn(newReference)

        `when`(
            receiptService.findByCreditorAndDate(CreditorTestData.createCreditorId(), TransactionTestData.VALUE_DAY),
        ).thenReturn(ReceiptTestData.createExistingReceipt())

        `when`(
            transactionEntityMapper.toNewTransaction(import, newReference, ReceiptTestData.createReceiptId()),
        ).thenReturn(newTransaction)

        `when`(
            transactionEntityMapper.toTransactionEntity(null, newTransaction),
        ).thenReturn(transactionEntity)

        transactionService.import(listOf(import))

        verify(transactionRepository).save(transactionEntity)
    }
}
