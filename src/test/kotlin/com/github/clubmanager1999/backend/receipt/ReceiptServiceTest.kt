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
package com.github.clubmanager1999.backend.receipt

import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.transaction.TransactionTestData
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
class ReceiptServiceTest {
    @Mock lateinit var receiptRepository: ReceiptRepository

    @InjectMocks lateinit var receiptService: ReceiptService

    @Test
    fun shouldGetReceiptById() {
        val existingReceipt = ReceiptTestData.createExistingReceipt()
        val savedEntity = ReceiptTestData.createReceiptEntity()

        `when`(receiptRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(receiptService.get(42)).isEqualTo(existingReceipt)
    }

    @Test
    fun shouldThrowExceptionIfReceiptIsNotFoundById() {
        `when`(receiptRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { receiptService.get(42) }
            .isInstanceOf(ReceiptNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllReceipts() {
        val existingReceipt = ReceiptTestData.createExistingReceipt()
        val savedEntity = ReceiptTestData.createReceiptEntity()

        `when`(receiptRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(receiptService.getAll()).containsExactly(existingReceipt)
    }

    @Test
    fun shouldCreateReceipt() {
        val newReceipt = ReceiptTestData.createNewReceipt()
        val existingReceipt = ReceiptTestData.createExistingReceipt()
        val savedEntity = ReceiptTestData.createReceiptEntity()
        val newEntity = ReceiptTestData.createFlatReceiptEntity().copy(id = null)

        `when`(receiptRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(receiptService.create(newReceipt)).isEqualTo(existingReceipt)
    }

    @Test
    fun shouldUpdateReceipt() {
        val newReceipt = ReceiptTestData.createNewReceipt()
        val existingReceipt = ReceiptTestData.createExistingReceipt()
        val savedEntity = ReceiptTestData.createReceiptEntity()
        val updatedEntity = ReceiptTestData.createFlatReceiptEntity()

        `when`(receiptRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(receiptRepository.save(updatedEntity)).thenReturn(savedEntity)

        assertThat(receiptService.update(42, newReceipt)).isEqualTo(existingReceipt)
    }

    @Test
    fun shouldThrowExceptionIfUpdateReceiptIsNotFound() {
        val newReceipt = ReceiptTestData.createNewReceipt()
        `when`(receiptRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { receiptService.update(42, newReceipt) }
            .isInstanceOf(ReceiptNotFoundException::class.java)

        verify(receiptRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteReceipt() {
        receiptService.delete(42)

        verify(receiptRepository).deleteById(42)
    }

    @Test
    fun shouldFindByCreditorAndDate() {
        val existingReceipt = ReceiptTestData.createExistingReceipt()
        val savedEntity = ReceiptTestData.createReceiptEntity()

        `when`(
            receiptRepository.findAllByCreditorAndDate(CreditorTestData.ID, TransactionTestData.VALUE_DAY),
        ).thenReturn(listOf(savedEntity))

        assertThat(
            receiptService.findByCreditorAndDate(CreditorTestData.createCreditorId(), TransactionTestData.VALUE_DAY),
        ).isEqualTo(existingReceipt)
    }

    @Test
    fun shouldFindFirstByCreditorAndDate() {
        val existingReceipt = ReceiptTestData.createExistingReceipt()
        val savedEntity = ReceiptTestData.createReceiptEntity()
        val secondEntity = ReceiptTestData.createReceiptEntity().copy(id = 1337)

        `when`(
            receiptRepository.findAllByCreditorAndDate(CreditorTestData.ID, TransactionTestData.VALUE_DAY),
        ).thenReturn(listOf(savedEntity, secondEntity))

        assertThat(
            receiptService.findByCreditorAndDate(CreditorTestData.createCreditorId(), TransactionTestData.VALUE_DAY),
        ).isEqualTo(existingReceipt)
    }
}
