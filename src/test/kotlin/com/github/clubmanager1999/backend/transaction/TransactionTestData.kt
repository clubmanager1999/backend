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

import java.math.BigDecimal
import java.time.LocalDate

object TransactionTestData {
    const val ID = 44L

    val BOOKING_DAY = LocalDate.of(2023, 11, 6)

    val VALUE_DAY = BOOKING_DAY.plusDays(1)

    const val NAME = "Robert Paulson"

    const val PURPOSE = "Soap order"

    val AMOUNT = BigDecimal("42.42")

    fun createNewTransaction(): NewTransaction {
        return NewTransaction(
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
        )
    }

    fun createExistingTransaction(): ExistingTransaction {
        return ExistingTransaction(
            id = ID,
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
        )
    }

    fun createTransactionEntity(): TransactionEntity {
        return TransactionEntity(
            id = ID,
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
        )
    }

    fun createTransactionImport(): TransactionImport {
        return TransactionImport(
            bookingDay = BOOKING_DAY,
            valueDay = VALUE_DAY,
            name = NAME,
            purpose = PURPOSE,
            amount = AMOUNT,
        )
    }
}
