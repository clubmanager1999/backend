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
package com.github.clubmanager1999.backend.domain.creditor

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
@ContextConfiguration(initializers = [CreditorRepositoryTest.DataSourceInitializer::class])
internal class CreditorRepositoryTest {
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

    @Autowired private lateinit var creditorRepository: CreditorRepository

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @BeforeEach
    fun beforeEach() {
        creditorRepository.deleteAll()
    }

    @Test
    fun shouldSaveCreditor() {
        creditorRepository.save(CreditorTestData.createCreditorEntity())

        assertThat(creditorRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(CreditorTestData.createCreditorEntity())
    }

    @Test
    fun shouldFindCreditorById() {
        val createdCreditor = creditorRepository.save(CreditorTestData.createCreditorEntity())

        assertThat(creditorRepository.findById(createdCreditor.id!!).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(CreditorTestData.createCreditorEntity())
    }

    @Test
    fun shouldUpdateCreditor() {
        val createdCreditor = creditorRepository.save(CreditorTestData.createCreditorEntity())
        creditorRepository.save(
            CreditorTestData.createCreditorEntity().copy(id = createdCreditor.id, name = "new"),
        )

        assertThat(creditorRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(CreditorTestData.createCreditorEntity().copy(name = "new"))
    }

    @Test
    fun shouldDeleteCreditor() {
        val createdCreditor = creditorRepository.save(CreditorTestData.createCreditorEntity())
        creditorRepository.deleteById(createdCreditor.id!!)

        assertThat(creditorRepository.findAll()).isEmpty()
    }
}
