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
import com.github.clubmanager1999.backend.receipt.ReceiptService
import com.github.clubmanager1999.backend.transaction.mapping.MappingService
import com.github.clubmanager1999.backend.transaction.reference.NewCreditorReference
import com.github.clubmanager1999.backend.transaction.reference.ReferenceEntityMapper
import org.springframework.stereotype.Service

@Service
class TransactionService(
    val transactionEntityMapper: TransactionEntityMapper,
    val transactionRepository: TransactionRepository,
    val mappingService: MappingService,
    val referenceEntityMapper: ReferenceEntityMapper,
    val receiptService: ReceiptService,
) {
    fun get(id: Long): ExistingTransaction {
        return transactionRepository
            .findById(id)
            .map { transactionEntityMapper.toExistingTransaction(it) }
            .orElseThrow { TransactionNotFoundException(id) }
    }

    fun getAll(): List<ExistingTransaction> {
        return transactionRepository.findAll().map { transactionEntityMapper.toExistingTransaction(it) }
    }

    fun create(newTransaction: NewTransaction): ExistingTransaction {
        return newTransaction
            .let { transactionEntityMapper.toTransactionEntity(null, it) }
            .let { transactionRepository.save(it) }
            .let { transactionEntityMapper.toExistingTransaction(it) }
    }

    fun update(
        id: Long,
        newTransaction: NewTransaction,
    ): ExistingTransaction {
        return transactionRepository
            .findById(id)
            .orElseThrow { TransactionNotFoundException(id) }
            .let { transactionEntityMapper.toTransactionEntity(id, newTransaction) }
            .let { transactionRepository.save(it) }
            .let { transactionEntityMapper.toExistingTransaction(it) }
    }

    fun delete(id: Long) {
        transactionRepository.deleteById(id)
    }

    fun import(transactionImports: List<TransactionImport>) {
        val existingTransactions = transactionRepository.findAll().map { it.transactionKey() }.toSet()
        val mappings = mappingService.getAll()

        fun toNewTransaction(transactionImport: TransactionImport): NewTransaction {
            val mapping = mappings.find { transactionImport.purpose.lowercase().contains(it.matcher.lowercase()) }
            val reference = mapping?.reference?.let { referenceEntityMapper.toNewReference(it) }
            val creditorReference = reference as? NewCreditorReference
            val receipt = creditorReference?.let { receiptService.findByCreditorAndDate(it.creditor, transactionImport.valueDay) }
            val receiptId = receipt?.let { ReceiptId(it.id) }

            return transactionEntityMapper.toNewTransaction(transactionImport, reference, receiptId)
        }

        transactionImports
            .filterNot { existingTransactions.contains(it.transactionKey()) }
            .map { toNewTransaction(it) }
            .map { transactionEntityMapper.toTransactionEntity(null, it) }
            .forEach { transactionRepository.save(it) }
    }
}
