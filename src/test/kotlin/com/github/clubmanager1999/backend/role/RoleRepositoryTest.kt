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
package com.github.clubmanager1999.backend.role

import com.github.clubmanager1999.backend.member.MemberRepository
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.membership.MembershipRepository
import com.github.clubmanager1999.backend.membership.MembershipTestData
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
@ContextConfiguration(initializers = [RoleRepositoryTest.DataSourceInitializer::class])
internal class RoleRepositoryTest {
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

    @Autowired private lateinit var roleRepository: RoleRepository

    @MockBean private lateinit var jwtDecoder: JwtDecoder

    @Autowired private lateinit var memberRepository: MemberRepository

    @Autowired private lateinit var membershipRepository: MembershipRepository

    private var memberEntity = MemberTestData.createMemberEntity()

    private var membershipEntity = MembershipTestData.createMembershipEntity()

    @BeforeEach
    fun beforeEach() {
        roleRepository.deleteAll()
        memberRepository.deleteAll()
        membershipRepository.deleteAll()
        membershipEntity = membershipRepository.save(MembershipTestData.createMembershipEntity())
        memberEntity = memberRepository.save(MemberTestData.createMemberEntity(membershipEntity))
    }

    @Test
    fun shouldSaveRole() {
        roleRepository.save(RoleTestData.createRoleEntity(memberEntity))

        assertThat(roleRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("holder.id")
            .ignoringFields("holder.membership.id")
            .isEqualTo(RoleTestData.createRoleEntity())
    }

    @Test
    fun shouldFindRoleById() {
        val createdRole = roleRepository.save(RoleTestData.createRoleEntity(memberEntity))

        assertThat(roleRepository.findById(createdRole.id!!).get())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("holder.id")
            .ignoringFields("holder.membership.id")
            .isEqualTo(RoleTestData.createRoleEntity())
    }

    @Test
    fun shouldUpdateRole() {
        val createdRole = roleRepository.save(RoleTestData.createRoleEntity(memberEntity))
        roleRepository.save(
            createdRole.copy(name = "new"),
        )

        assertThat(roleRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .ignoringFields("holder.id")
            .ignoringFields("holder.membership.id")
            .isEqualTo(RoleTestData.createRoleEntity().copy(name = "new"))
    }

    @Test
    fun shouldDeleteRole() {
        val createdRole = roleRepository.save(RoleTestData.createRoleEntity(memberEntity))
        roleRepository.deleteById(createdRole.id!!)

        assertThat(roleRepository.findAll()).isEmpty()
    }
}
