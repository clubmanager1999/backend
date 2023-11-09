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

import com.github.clubmanager1999.backend.creditor.CreditorRepository
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.donor.DonorRepository
import com.github.clubmanager1999.backend.donor.DonorTestData
import com.github.clubmanager1999.backend.member.MemberRepository
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.membership.MembershipRepository
import com.github.clubmanager1999.backend.membership.MembershipTestData
import com.github.clubmanager1999.backend.receipt.ReceiptRepository
import com.github.clubmanager1999.backend.receipt.ReceiptTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension::class)
@ContextConfiguration(initializers = [TransactionRepositoryTest.DataSourceInitializer::class])
internal class TransactionRepositoryTest {
    companion object {
        @Container val postgresqlContainer = PostgreSQLContainer("postgres")
    }

    class DataSourceInitializer : ApplicationContextInitializer<ConfigurableApplicationContext?> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=" + postgresqlContainer.jdbcUrl,
                "spring.datasource.username=" + postgresqlContainer.username,
                "spring.datasource.password=" + postgresqlContainer.password,
                "spring.datasource.password=" + postgresqlContainer.password,
                "spring.jpa.hibernate.ddl-auto=update",
            )
        }
    }

    @Autowired private lateinit var transactionRepository: TransactionRepository

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Autowired private lateinit var memberRepository: MemberRepository

    @Autowired private lateinit var membershipRepository: MembershipRepository

    @Autowired private lateinit var donorRepository: DonorRepository

    @Autowired private lateinit var creditorRepository: CreditorRepository

    @Autowired private lateinit var receiptRepository: ReceiptRepository

    private var memberEntity = MemberTestData.createMemberEntity()
    private var membershipEntity = MembershipTestData.createMembershipEntity()
    private var donorEntity = DonorTestData.createDonorEntity()
    private var creditorEntity = CreditorTestData.createCreditorEntity()
    private var receiptEntity = ReceiptTestData.createReceiptEntity()

    @BeforeEach
    fun beforeEach() {
        transactionRepository.deleteAll()
        memberRepository.deleteAll()
        membershipRepository.deleteAll()
        memberRepository.deleteAll()
        donorRepository.deleteAll()
        receiptRepository.deleteAll()
        creditorRepository.deleteAll()
        membershipEntity = membershipRepository.save(MembershipTestData.createMembershipEntity())
        memberEntity = memberRepository.save(MemberTestData.createMemberEntity(membershipEntity))
        donorEntity = donorRepository.save(DonorTestData.createDonorEntity())
        creditorEntity = creditorRepository.save(CreditorTestData.createCreditorEntity())
        receiptEntity = receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))
    }

    @Test
    fun shouldSaveTransaction() {
        transactionRepository.save(TransactionTestData.createTransactionEntity(memberEntity, donorEntity, creditorEntity, receiptEntity))

        assertThat(transactionRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("member.id")
            .ignoringFields("member.membership.id")
            .ignoringFields("donor.id")
            .ignoringFields("creditor.id")
            .ignoringFields("receipt.id")
            .ignoringFields("receipt.creditor.id")
            .isEqualTo(TransactionTestData.createTransactionEntity())
    }

    @Test
    fun shouldFindTransactionById() {
        val createdTransaction =
            transactionRepository.save(
                TransactionTestData.createTransactionEntity(memberEntity, donorEntity, creditorEntity, receiptEntity),
            )

        assertThat(transactionRepository.findById(createdTransaction.id!!).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("member.id")
            .ignoringFields("member.membership.id")
            .ignoringFields("donor.id")
            .ignoringFields("creditor.id")
            .ignoringFields("receipt.id")
            .ignoringFields("receipt.creditor.id")
            .isEqualTo(TransactionTestData.createTransactionEntity())
    }

    @Test
    fun shouldUpdateTransaction() {
        val createdTransaction =
            transactionRepository.save(
                TransactionTestData.createTransactionEntity(memberEntity, donorEntity, creditorEntity, receiptEntity),
            )

        transactionRepository.save(
            TransactionTestData.createTransactionEntity(
                memberEntity,
                donorEntity,
                creditorEntity,
                receiptEntity,
            ).copy(id = createdTransaction.id, name = "new"),
        )

        assertThat(transactionRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("member.id")
            .ignoringFields("member.membership.id")
            .ignoringFields("donor.id")
            .ignoringFields("creditor.id")
            .ignoringFields("receipt.id")
            .ignoringFields("receipt.creditor.id")
            .isEqualTo(TransactionTestData.createTransactionEntity().copy(name = "new"))
    }

    @Test
    fun shouldDeleteTransaction() {
        val createdTransaction =
            transactionRepository.save(
                TransactionTestData.createTransactionEntity(memberEntity, donorEntity, creditorEntity, receiptEntity),
            )
        transactionRepository.deleteById(createdTransaction.id!!)

        assertThat(transactionRepository.findAll()).isEmpty()
    }
}
