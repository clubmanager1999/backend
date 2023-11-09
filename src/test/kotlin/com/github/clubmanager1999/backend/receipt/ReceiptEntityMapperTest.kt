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

import com.github.clubmanager1999.backend.creditor.CreditorEntityMapper
import com.github.clubmanager1999.backend.creditor.CreditorId
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.receipt.ReceiptTestData.ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ReceiptEntityMapperTest {
    @Mock lateinit var creditorEntityMapper: CreditorEntityMapper

    @InjectMocks lateinit var receiptEntityMapper: ReceiptEntityMapper

    @Test
    fun shouldMapReceiptEntityToExistingReceipt() {
        `when`(
            creditorEntityMapper.toExistingCreditor(CreditorTestData.createCreditorEntity()),
        ).thenReturn(CreditorTestData.createExistingCreditor())

        assertThat(
            receiptEntityMapper.toExistingReceipt(
                ReceiptTestData.createReceiptEntity(),
            ),
        )
            .isEqualTo(ReceiptTestData.createExistingReceipt())
    }

    @Test
    fun shouldMapNewReceiptToReceiptEntityWithId() {
        `when`(creditorEntityMapper.toCreditorEntity(CreditorId(CreditorTestData.ID))).thenReturn(CreditorTestData.createCreditorEntity())

        assertThat(
            receiptEntityMapper.toReceiptEntity(ID, ReceiptTestData.createNewReceipt()),
        )
            .isEqualTo(ReceiptTestData.createReceiptEntity())
    }

    @Test
    fun shouldMapReceiptIdToReceiptEntity() {
        assertThat(
            receiptEntityMapper.toReceiptEntity(ReceiptId(ID)).id,
        )
            .isEqualTo(ID)
    }
}
