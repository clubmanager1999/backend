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
package com.github.clubmanager1999.backend.election

import com.github.clubmanager1999.backend.election.ElectionTestData.VALID_FROM
import com.github.clubmanager1999.backend.member.MemberRepository
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.membership.MembershipRepository
import com.github.clubmanager1999.backend.membership.MembershipTestData
import com.github.clubmanager1999.backend.role.RoleRepository
import com.github.clubmanager1999.backend.role.RoleTestData
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
@ContextConfiguration(initializers = [ElectionRepositoryTest.DataSourceInitializer::class])
internal class ElectionRepositoryTest {
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

    @Autowired private lateinit var electionRepository: ElectionRepository

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Autowired private lateinit var roleRepository: RoleRepository

    @Autowired private lateinit var memberRepository: MemberRepository

    @Autowired private lateinit var membershipRepository: MembershipRepository

    private var roleEntity = RoleTestData.createRoleEntity()
    private var memberEntity = MemberTestData.createMemberEntity()
    private var membershipEntity = MembershipTestData.createMembershipEntity()

    @BeforeEach
    fun beforeEach() {
        electionRepository.deleteAll()
        roleRepository.deleteAll()
        memberRepository.deleteAll()
        membershipRepository.deleteAll()

        membershipEntity = membershipRepository.save(MembershipTestData.createMembershipEntity())
        memberEntity = memberRepository.save(MemberTestData.createMemberEntity(membershipEntity))
        roleEntity = roleRepository.save(RoleTestData.createRoleEntity(memberEntity))
    }

    @Test
    fun shouldSaveElection() {
        electionRepository.save(ElectionTestData.createElectionEntity(roleEntity, memberEntity))

        assertThat(electionRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("role.id")
            .ignoringFields("role.holder.id")
            .ignoringFields("role.holder.membership.id")
            .ignoringFields("member.id")
            .ignoringFields("member.membership.id")
            .isEqualTo(ElectionTestData.createElectionEntity())
    }

    @Test
    fun shouldFindElectionById() {
        val createdElection = electionRepository.save(ElectionTestData.createElectionEntity(roleEntity, memberEntity))

        assertThat(electionRepository.findById(createdElection.id!!).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("role.id")
            .ignoringFields("role.holder.id")
            .ignoringFields("role.holder.membership.id")
            .ignoringFields("member.id")
            .ignoringFields("member.membership.id")
            .isEqualTo(ElectionTestData.createElectionEntity())
    }

    @Test
    fun shouldUpdateElection() {
        val createdElection = electionRepository.save(ElectionTestData.createElectionEntity(roleEntity, memberEntity))
        electionRepository.save(
            ElectionTestData.createElectionEntity(
                roleEntity,
                memberEntity,
            ).copy(id = createdElection.id, validFrom = VALID_FROM.plusDays(5)),
        )

        assertThat(electionRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("role.id")
            .ignoringFields("role.holder.id")
            .ignoringFields("role.holder.membership.id")
            .ignoringFields("member.id")
            .ignoringFields("member.membership.id")
            .isEqualTo(ElectionTestData.createElectionEntity().copy(validFrom = VALID_FROM.plusDays(5)))
    }

    @Test
    fun shouldDeleteElection() {
        val createdElection = electionRepository.save(ElectionTestData.createElectionEntity(roleEntity, memberEntity))
        electionRepository.deleteById(createdElection.id!!)

        assertThat(electionRepository.findAll()).isEmpty()
    }
}
