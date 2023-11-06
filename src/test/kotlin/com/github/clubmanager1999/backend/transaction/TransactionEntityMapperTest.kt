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

import com.github.clubmanager1999.backend.transaction.TransactionTestData.ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TransactionEntityMapperTest {
    @InjectMocks lateinit var transactionEntityMapper: TransactionEntityMapper

    @Test
    fun shouldMapTransactionEntityToExistingTransaction() {
        assertThat(
            transactionEntityMapper.toExistingTransaction(
                TransactionTestData.createTransactionEntity(),
            ),
        )
            .isEqualTo(TransactionTestData.createExistingTransaction())
    }

    @Test
    fun shouldMapExistingTransactionToTransactionEntity() {
        assertThat(
            transactionEntityMapper.toTransactionEntity(
                TransactionTestData.createExistingTransaction(),
            ),
        )
            .isEqualTo(TransactionTestData.createTransactionEntity())
    }

    @Test
    fun shouldMapNewTransactionToTransactionEntityWithId() {
        assertThat(
            transactionEntityMapper.toTransactionEntity(ID, TransactionTestData.createNewTransaction()),
        )
            .isEqualTo(TransactionTestData.createTransactionEntity())
    }

    @Test
    fun shouldMapTransactionImportToTransactionEntityWithId() {
        assertThat(
            transactionEntityMapper.toTransactionEntity(TransactionTestData.createTransactionImport()),
        )
            .isEqualTo(TransactionTestData.createTransactionEntity().copy(id = null))
    }
}
