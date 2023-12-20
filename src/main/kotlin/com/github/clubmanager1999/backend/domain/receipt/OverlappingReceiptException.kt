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

import com.github.clubmanager1999.backend.error.BusinessException
import com.github.clubmanager1999.backend.error.ErrorCode
import org.springframework.http.HttpStatus

class OverlappingReceiptException : BusinessException(
    HttpStatus.CONFLICT,
    ErrorCode.OVERLAPPING_RECEIPT,
    "There is already an receipt with a similar range",
)
