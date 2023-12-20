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
package com.github.clubmanager1999.backend.domain.receipt

import com.github.clubmanager1999.backend.domain.creditor.CreditorTestData
import com.github.clubmanager1999.backend.domain.creditor.toCreditorEntity
import com.github.clubmanager1999.backend.domain.receipt.ReceiptTestData.ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ReceiptMappingExtensionsTest {
    @Test
    fun shouldMapReceiptEntityToExistingReceipt() {
        assertThat(
            ReceiptTestData.createReceiptEntity().toExistingReceipt(),
        )
            .isEqualTo(ReceiptTestData.createExistingReceipt())
    }

    @Test
    fun shouldMapNewReceiptToReceiptEntityWithId() {
        assertThat(
            ReceiptTestData.createNewReceipt().toReceiptEntity(ID),
        )
            .isEqualTo(ReceiptTestData.createReceiptEntity(CreditorTestData.createCreditorId().toCreditorEntity()))
    }

    @Test
    fun shouldMapReceiptIdToReceiptEntity() {
        assertThat(
            ReceiptId(ID).toReceiptEntity().id,
        )
            .isEqualTo(ID)
    }
}
