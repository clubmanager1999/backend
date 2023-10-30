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
package com.github.clubmanager1999.backend.member

import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
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
@ContextConfiguration(initializers = [MemberRepositoryTest.DataSourceInitializer::class])
internal class MemberRepositoryTest {
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

    @Autowired private lateinit var memberRepository: MemberRepository

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
    }

    @Test
    fun shouldSaveMember() {
        memberRepository.save(MemberTestData.createMemberEntity())

        assertThat(memberRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(MemberTestData.createMemberEntity())
    }

    @Test
    fun shouldFindMemberById() {
        val createdMember = memberRepository.save(MemberTestData.createMemberEntity())

        assertThat(memberRepository.findById(createdMember.id!!).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(MemberTestData.createMemberEntity())
    }

    @Test
    fun shouldFindMemberBySubject() {
        memberRepository.save(MemberTestData.createMemberEntity())

        assertThat(memberRepository.findBySubject(SUBJECT).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(MemberTestData.createMemberEntity())
    }

    @Test
    fun shouldUpdateMember() {
        val createdMember = memberRepository.save(MemberTestData.createMemberEntity())
        memberRepository.save(
            MemberTestData.createMemberEntity()
                .copy(id = createdMember.id, firstName = "new"),
        )

        assertThat(memberRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(MemberTestData.createMemberEntity().copy(firstName = "new"))
    }

    @Test
    fun shouldDeleteMember() {
        val createdMember = memberRepository.save(MemberTestData.createMemberEntity())
        memberRepository.deleteById(createdMember.id!!)

        assertThat(memberRepository.findAll()).isEmpty()
    }
}
