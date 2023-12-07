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

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import jakarta.ws.rs.WebApplicationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.InputStream

@ControllerAdvice
class ControllerExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<ApiError> {
        logger.info("Handled ${e.javaClass.simpleName}", e)

        return ResponseEntity(
            ApiError(ErrorCode.INTERNAL_ERROR, "Internal error"),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    @ExceptionHandler(WebApplicationException::class)
    fun handle(e: WebApplicationException): ResponseEntity<ApiError> {
        var body: String? = null

        try {
            val inputStream = e.response.entity as InputStream
            body = inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            logger.warn("Failed to get response body", e)
        }

        logger.info("Handled ${e.javaClass.simpleName}: $body", e)

        return ResponseEntity(
            ApiError(ErrorCode.INTERNAL_ERROR, "Internal error"),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(e: HttpMessageNotReadableException): ResponseEntity<ApiError> {
        val cause = e.cause
        var fieldErrors: Map<String, String?>? = null

        if (cause is MissingKotlinParameterException) {
            val name = cause.parameter.name

            if (name != null) {
                fieldErrors = mapOf(name to "must not be null")
            }
        }

        return ResponseEntity(
            ApiError(ErrorCode.VALIDATION_ERROR, "Validation error", fieldErrors),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(BusinessException::class)
    fun handle(e: BusinessException): ResponseEntity<ApiError> {
        return ResponseEntity(
            ApiError(e.errorCode, e.errorMessage),
            e.status,
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(e: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val fieldErrors =
            e.fieldErrors
                .groupBy { it.field }
                .mapValues { pair -> pair.value.map { it.defaultMessage }.firstOrNull() }

        return ResponseEntity(
            ApiError(ErrorCode.VALIDATION_ERROR, "Validation error", fieldErrors),
            HttpStatus.BAD_REQUEST,
        )
    }
}
