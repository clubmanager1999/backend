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
package com.github.clubmanager1999.backend.error

import com.github.clubmanager1999.backend.member.MemberNotFoundException
import com.github.clubmanager1999.backend.member.SubjectNotFoundException
import com.github.clubmanager1999.backend.membership.MembershipNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<ApiError> {
        return ResponseEntity(
            ApiError(ErrorCode.INTERNAL_ERROR, "Internal error"),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    @ExceptionHandler(MemberNotFoundException::class)
    fun handle(e: MemberNotFoundException): ResponseEntity<ApiError> {
        return ResponseEntity(
            ApiError(ErrorCode.MEMBER_NOT_FOUND, e.message!!),
            HttpStatus.NOT_FOUND,
        )
    }

    @ExceptionHandler(SubjectNotFoundException::class)
    fun handle(e: SubjectNotFoundException): ResponseEntity<ApiError> {
        return ResponseEntity(
            ApiError(ErrorCode.SUBJECT_NOT_FOUND, e.message!!),
            HttpStatus.NOT_FOUND,
        )
    }

    @ExceptionHandler(MembershipNotFoundException::class)
    fun handle(e: MembershipNotFoundException): ResponseEntity<ApiError> {
        return ResponseEntity(
            ApiError(ErrorCode.MEMBERSHIP_NOT_FOUND, e.message!!),
            HttpStatus.NOT_FOUND,
        )
    }
}
