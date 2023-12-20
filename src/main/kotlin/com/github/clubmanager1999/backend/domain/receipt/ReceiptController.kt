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

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

@RestController
class ReceiptController(val receiptService: ReceiptService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/receipts/{id}").build()

    @GetMapping("/api/receipts/{id}")
    fun getReceipt(
        @PathVariable id: Long,
    ): ExistingReceipt {
        return receiptService.get(id)
    }

    @GetMapping("/api/receipts")
    fun getAllReceipts(): List<ExistingReceipt> {
        return receiptService.getAll()
    }

    @PostMapping("/api/receipts")
    fun createReceipt(
        @RequestBody @Valid newReceipt: NewReceipt,
    ): ResponseEntity<Void> {
        val receiptId = receiptService.create(newReceipt)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(receiptId.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/receipts/{id}")
    fun updateReceipt(
        @PathVariable id: Long,
        @RequestBody @Valid newReceipt: NewReceipt,
    ): ResponseEntity<Void> {
        receiptService.update(id, newReceipt)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/receipts/{id}")
    fun deleteReceipt(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        receiptService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
