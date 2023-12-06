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

import com.github.clubmanager1999.backend.receipt.ReceiptId
import com.github.clubmanager1999.backend.transaction.area.AreaId
import com.github.clubmanager1999.backend.transaction.purpose.PurposeId
import com.github.clubmanager1999.backend.transaction.reference.NewReference
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class NewTransaction(
    @field:NotNull
    val bookingDay: LocalDate,
    @field:NotNull
    val valueDay: LocalDate,
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String,
    @field:NotNull
    val amount: BigDecimal,
    @field:Valid
    @field:NotNull
    val reference: NewReference?,
    @field:Valid
    @field:NotNull
    val receipt: ReceiptId?,
    @field:Valid
    @field:NotNull
    val purpose: PurposeId?,
    @field:Valid
    @field:NotNull
    val area: AreaId?,
)
