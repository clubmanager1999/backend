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
package com.github.clubmanager1999.backend.domain.election

import com.github.clubmanager1999.backend.domain.member.MemberId
import com.github.clubmanager1999.backend.domain.role.RoleId
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class NewElection(
    @field:Valid
    @field:NotNull
    val role: RoleId,
    @field:Valid
    @field:NotNull
    val member: MemberId,
    @field:NotNull
    val validFrom: LocalDate,
    @field:NotNull
    val validTo: LocalDate?,
)
