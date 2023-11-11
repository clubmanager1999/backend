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

import com.github.clubmanager1999.backend.creditor.CreditorEntity
import com.github.clubmanager1999.backend.creditor.CreditorId
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import java.time.LocalDate

object ReceiptTestData {
    const val ID = 46L

    const val NAME = "Ikea"

    val VALID_FROM = LocalDate.of(2023, 11, 6)

    val VALID_TO = VALID_FROM.plusDays(2)

    fun createNewReceipt(): NewReceipt {
        return NewReceipt(
            name = NAME,
            validFrom = VALID_FROM,
            validTo = VALID_TO,
            creditor = CreditorId(CreditorTestData.ID),
        )
    }

    fun createExistingReceipt(): ExistingReceipt {
        return ExistingReceipt(
            id = ID,
            name = NAME,
            validFrom = VALID_FROM,
            validTo = VALID_TO,
            creditor = CreditorTestData.createExistingCreditor(),
        )
    }

    fun createReceiptEntity(): ReceiptEntity {
        return createReceiptEntity(CreditorTestData.createCreditorEntity())
    }

    fun createReceiptEntity(creditorEntity: CreditorEntity): ReceiptEntity {
        return ReceiptEntity(
            id = ID,
            name = NAME,
            validFrom = VALID_FROM,
            validTo = VALID_TO,
            creditor = creditorEntity,
        )
    }

    fun createReceiptId(): ReceiptId {
        return ReceiptId(ID)
    }
}
