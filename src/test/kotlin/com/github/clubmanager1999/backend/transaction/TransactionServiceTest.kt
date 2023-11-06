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
        `when`(transactionRepository.findAll()).thenReturn(emptyList())

        `when`(
            transactionEntityMapper.toTransactionEntity(TransactionTestData.createTransactionImport()),
        ).thenReturn(TransactionTestData.createTransactionEntity())

        transactionService.import(listOf(TransactionTestData.createTransactionImport()))

        verify(transactionRepository).save(TransactionTestData.createTransactionEntity())
    }

    @Test
    fun shouldNotImportExistingTransactions() {
        `when`(transactionRepository.findAll()).thenReturn(listOf(TransactionTestData.createTransactionEntity()))

        transactionService.import(listOf(TransactionTestData.createTransactionImport()))

        verify(transactionRepository, never()).save(any())
    }
}
