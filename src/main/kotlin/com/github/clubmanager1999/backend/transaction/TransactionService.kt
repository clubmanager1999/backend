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

import org.springframework.stereotype.Service

@Service
class TransactionService(
    val transactionEntityMapper: TransactionEntityMapper,
    val transactionRepository: TransactionRepository,
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

        transactionImports
            .filterNot { existingTransactions.contains(it.transactionKey()) }
            .map { transactionEntityMapper.toTransactionEntity(it) }
            .forEach { transactionRepository.save(it) }
    }
}
