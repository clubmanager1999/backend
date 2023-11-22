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

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

@RestController
class TransactionController(val transactionService: TransactionService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/transactions/{id}").build()

    @GetMapping("/api/transactions/{id}")
    fun getTransaction(
        @PathVariable id: Long,
    ): ExistingTransaction {
        return transactionService.get(id)
    }

    @GetMapping("/api/transactions")
    fun getAllTransactions(
        @RequestParam year: Int?,
    ): List<ExistingTransaction> {
        return if (year == null) {
            transactionService.getAll()
        } else {
            transactionService.getAllByYear(year)
        }
    }

    @PostMapping("/api/transactions")
    fun createTransaction(
        @RequestBody newTransaction: NewTransaction,
    ): ResponseEntity<Void> {
        val existingTransaction = transactionService.create(newTransaction)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(existingTransaction.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/transactions/{id}")
    fun updateTransaction(
        @PathVariable id: Long,
        @RequestBody newTransaction: NewTransaction,
    ): ResponseEntity<Void> {
        transactionService.update(id, newTransaction)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/transactions/{id}")
    fun deleteTransaction(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        transactionService.delete(id)

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/transactions/imports")
    fun import(
        @RequestBody transactionImports: List<TransactionImport>,
    ): ResponseEntity<Void> {
        transactionService.import(transactionImports)

        return ResponseEntity.noContent().build()
    }
}
