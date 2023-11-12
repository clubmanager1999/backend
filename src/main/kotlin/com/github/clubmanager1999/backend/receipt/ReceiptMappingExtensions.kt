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
import com.github.clubmanager1999.backend.creditor.toCreditorEntity
import com.github.clubmanager1999.backend.creditor.toExistingCreditor
import java.time.LocalDate

fun ReceiptEntity.toExistingReceipt(): ExistingReceipt {
    return ExistingReceipt(
        id = this.id!!,
        name = this.name,
        validFrom = this.validFrom,
        validTo = this.validTo,
        creditor = this.creditor.toExistingCreditor(),
    )
}

fun NewReceipt.toReceiptEntity(id: Long?): ReceiptEntity {
    return ReceiptEntity(
        id = id,
        name = this.name,
        validFrom = this.validFrom,
        validTo = this.validTo,
        creditor = this.creditor.toCreditorEntity(),
    )
}

fun ReceiptId.toReceiptEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = this.id,
        name = "",
        validFrom = LocalDate.MIN,
        validTo = LocalDate.MIN,
        creditor = CreditorEntity(null, ""),
    )
}
