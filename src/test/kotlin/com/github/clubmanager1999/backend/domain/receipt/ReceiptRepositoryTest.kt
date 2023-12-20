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

import com.github.clubmanager1999.backend.domain.creditor.CreditorRepository
import com.github.clubmanager1999.backend.domain.creditor.CreditorTestData
import com.github.clubmanager1999.backend.domain.receipt.ReceiptTestData.VALID_FROM
import com.github.clubmanager1999.backend.domain.receipt.ReceiptTestData.VALID_TO
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
@ContextConfiguration(initializers = [ReceiptRepositoryTest.DataSourceInitializer::class])
internal class ReceiptRepositoryTest {
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

    @Autowired private lateinit var receiptRepository: ReceiptRepository

    @Autowired private lateinit var creditorRepository: CreditorRepository

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    private var creditorEntity = CreditorTestData.createCreditorEntity()

    @BeforeEach
    fun beforeEach() {
        receiptRepository.deleteAll()
        creditorRepository.deleteAll()
        creditorEntity = creditorRepository.save(CreditorTestData.createCreditorEntity())
    }

    @Test
    fun shouldSaveReceipt() {
        receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receiptRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(ReceiptTestData.createReceiptEntity(creditorEntity))
    }

    @Test
    fun shouldFindReceiptById() {
        val createdReceipt = receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receiptRepository.findById(createdReceipt.id!!).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(ReceiptTestData.createReceiptEntity(creditorEntity))
    }

    @Test
    fun shouldUpdateReceipt() {
        val createdReceipt = receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))
        receiptRepository.save(
            ReceiptTestData.createReceiptEntity().copy(id = createdReceipt.id, name = "new", creditor = creditorEntity),
        )

        assertThat(receiptRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(ReceiptTestData.createReceiptEntity().copy(name = "new", creditor = creditorEntity))
    }

    @Test
    fun shouldDeleteReceipt() {
        val createdReceipt = receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))
        receiptRepository.deleteById(createdReceipt.id!!)

        assertThat(receiptRepository.findAll()).isEmpty()
    }

    @Test
    fun shouldFindByCreditorAndDateEqValidFrom() {
        receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receiptRepository.findAllByCreditorAndDate(creditorEntity.id!!, VALID_FROM)[0])
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(ReceiptTestData.createReceiptEntity(creditorEntity))
    }

    @Test
    fun shouldFindBothByCreditorAndDateEqValidFrom() {
        val receipt1 = receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))
        val receipt2 = receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receipt1.id).isNotEqualTo(receipt2.id)

        assertThat(receiptRepository.findAllByCreditorAndDate(creditorEntity.id!!, VALID_FROM))
            .containsExactly(receipt1, receipt2)
    }

    @Test
    fun shouldFindByCreditorAndDateGtValidFrom() {
        receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receiptRepository.findAllByCreditorAndDate(creditorEntity.id!!, VALID_FROM.plusDays(1))[0])
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(ReceiptTestData.createReceiptEntity(creditorEntity))
    }

    @Test
    fun shouldFindByCreditorAndDateEqValidTo() {
        receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receiptRepository.findAllByCreditorAndDate(creditorEntity.id!!, VALID_TO)[0])
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(ReceiptTestData.createReceiptEntity(creditorEntity))
    }

    @Test
    fun shouldNotFindByCreditorAndDateLtValidFrom() {
        receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receiptRepository.findAllByCreditorAndDate(creditorEntity.id!!, VALID_FROM.minusDays(1)))
            .isEmpty()
    }

    @Test
    fun shouldNotFindByCreditorAndDateGtValidTo() {
        receiptRepository.save(ReceiptTestData.createReceiptEntity(creditorEntity))

        assertThat(receiptRepository.findAllByCreditorAndDate(creditorEntity.id!!, VALID_TO.plusDays(2)))
            .isEmpty()
    }
}
