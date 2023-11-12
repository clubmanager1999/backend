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
package com.github.clubmanager1999.backend.transaction.purpose

import com.github.clubmanager1999.backend.creditor.CreditorRepository
import com.github.clubmanager1999.backend.creditor.CreditorTestData
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
@ContextConfiguration(initializers = [PurposeRepositoryTest.DataSourceInitializer::class])
internal class PurposeRepositoryTest {
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

    @Autowired private lateinit var purposeRepository: PurposeRepository

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Autowired private lateinit var creditorRepository: CreditorRepository

    private var creditorEntity = CreditorTestData.createCreditorEntity()

    @BeforeEach
    fun beforeEach() {
        purposeRepository.deleteAll()
        creditorRepository.deleteAll()
        creditorEntity = creditorRepository.save(CreditorTestData.createCreditorEntity())
    }

    @Test
    fun shouldSavePurpose() {
        purposeRepository.save(PurposeTestData.createPurposeEntity())

        assertThat(purposeRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(PurposeTestData.createPurposeEntity())
    }

    @Test
    fun shouldFindPurposeById() {
        val createdPurpose = purposeRepository.save(PurposeTestData.createPurposeEntity())

        assertThat(purposeRepository.findById(createdPurpose.id!!).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(PurposeTestData.createPurposeEntity())
    }

    @Test
    fun shouldUpdatePurpose() {
        val createdPurpose = purposeRepository.save(PurposeTestData.createPurposeEntity())
        purposeRepository.save(
            PurposeTestData.createPurposeEntity().copy(id = createdPurpose.id, name = "new"),
        )

        assertThat(purposeRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(PurposeTestData.createPurposeEntity().copy(name = "new"))
    }

    @Test
    fun shouldDeletePurpose() {
        val createdPurpose = purposeRepository.save(PurposeTestData.createPurposeEntity())
        purposeRepository.deleteById(createdPurpose.id!!)

        assertThat(purposeRepository.findAll()).isEmpty()
    }
}
