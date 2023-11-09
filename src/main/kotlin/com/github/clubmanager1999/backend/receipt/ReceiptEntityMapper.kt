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
import org.springframework.stereotype.Service

@Service
class ReceiptEntityMapper(val creditorEntityMapper: CreditorEntityMapper) {
    fun toExistingReceipt(receiptEntity: ReceiptEntity): ExistingReceipt {
        return ExistingReceipt(
            id = receiptEntity.id!!,
            name = receiptEntity.name,
            validFrom = receiptEntity.validFrom,
            validTo = receiptEntity.validTo,
            creditor = creditorEntityMapper.toExistingCreditor(receiptEntity.creditor),
        )
    }

    fun toReceiptEntity(
        id: Long?,
        newReceipt: NewReceipt,
    ): ReceiptEntity {
        return ReceiptEntity(
            id = id,
            name = newReceipt.name,
            validFrom = newReceipt.validFrom,
            validTo = newReceipt.validTo,
            creditor = creditorEntityMapper.toCreditorEntity(newReceipt.creditor),
        )
    }
}
