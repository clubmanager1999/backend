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

import com.github.clubmanager1999.backend.receipt.ReceiptTestData
import com.github.clubmanager1999.backend.transaction.TransactionTestData.ID
import com.github.clubmanager1999.backend.transaction.purpose.PurposeTestData
import com.github.clubmanager1999.backend.transaction.reference.ReferenceTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TransactionMappingExtensionsTest {
    @Test
    fun shouldMapTransactionEntityToExistingTransaction() {
        assertThat(
            TransactionTestData.createTransactionEntity().toExistingTransaction(),
        )
            .isEqualTo(TransactionTestData.createExistingTransaction())
    }

    @Test
    fun shouldMapTransactionEntityToExistingTransactionWithoutOtherEntities() {
        assertThat(
            TransactionTestData.createTransactionEntity().copy(reference = null, receipt = null, purpose = null).toExistingTransaction(),
        )
            .isEqualTo(
                TransactionTestData.createExistingTransaction().copy(reference = null, receipt = null, purpose = null),
            )
    }

    @Test
    fun shouldMapNewTransactionToTransactionEntityWithId() {
        assertThat(
            TransactionTestData.createNewTransaction().toTransactionEntity(ID),
        )
            .isEqualTo(TransactionTestData.createFlatTransactionEntity())
    }

    @Test
    fun shouldMapNewTransactionToTransactionEntityWithIdWithoutOtherEntities() {
        assertThat(
            TransactionTestData.createNewTransaction().copy(reference = null, receipt = null, purpose = null).toTransactionEntity(
                ID,
            ),
        )
            .isEqualTo(TransactionTestData.createTransactionEntity().copy(reference = null, receipt = null, purpose = null))
    }

    @Test
    fun shouldMapTransactionImportToNewTransaction() {
        assertThat(
            TransactionTestData.createTransactionImport().toNewTransaction(
                ReferenceTestData.createNewReference(),
                ReceiptTestData.createReceiptId(),
                PurposeTestData.createPurposeId(),
            ),
        )
            .isEqualTo(TransactionTestData.createNewTransaction())
    }
}
