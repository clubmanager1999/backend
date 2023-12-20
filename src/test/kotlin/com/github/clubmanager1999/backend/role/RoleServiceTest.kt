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

import com.github.clubmanager1999.backend.election.ElectionService
import com.github.clubmanager1999.backend.member.MemberNotFoundException
import com.github.clubmanager1999.backend.member.MemberRepository
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.member.toMemberEntity
import com.github.clubmanager1999.backend.oidc.OidcAdminService
import com.github.clubmanager1999.backend.oidc.Subject
import com.github.clubmanager1999.backend.role.RoleTestData.ID
import com.github.clubmanager1999.backend.role.RoleTestData.NAME
import com.github.clubmanager1999.backend.security.Permission
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verifyNoInteractions
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class RoleServiceTest {
    @Mock lateinit var roleRepository: RoleRepository

    @Mock lateinit var memberRepository: MemberRepository

    @Mock lateinit var oidcAdminService: OidcAdminService

    @Mock lateinit var electionService: ElectionService

    @InjectMocks lateinit var roleService: RoleService

    @Test
    fun shouldGetRoleById() {
        val existingRole = RoleTestData.createExistingRole()
        val savedEntity = RoleTestData.createRoleEntity()

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        assertThat(roleService.get(ID)).isEqualTo(existingRole)
    }

    @Test
    fun shouldThrowExceptionIfRoleIsNotFoundById() {
        `when`(roleRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { roleService.get(ID) }
            .isInstanceOf(RoleNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllRoles() {
        val existingRole = RoleTestData.createExistingRole()
        val savedEntity = RoleTestData.createRoleEntity()

        `when`(roleRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(roleService.getAll()).containsExactly(existingRole)
    }

    @Test
    fun shouldCreateRole() {
        val newRole = RoleTestData.createNewRole()
        val savedEntity = RoleTestData.createRoleEntity()
        val newEntity = savedEntity.copy(id = null, holder = null)

        `when`(roleRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(roleService.create(newRole)).isEqualTo(RoleTestData.createRoleId())

        verify(oidcAdminService).createRole(NAME)
    }

    @Test
    fun shouldUpdateRole() {
        val newRole = RoleTestData.createNewRole()
        val savedEntity = RoleTestData.createRoleEntity()
        val newEntity = savedEntity.copy(holder = null)

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(roleRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(roleService.update(ID, newRole)).isEqualTo(RoleTestData.createRoleId())
    }

    @Test
    fun shouldThrowExceptionIfUpdateRoleIsNotFound() {
        val newRole = RoleTestData.createNewRole()
        `when`(roleRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { roleService.update(ID, newRole) }
            .isInstanceOf(RoleNotFoundException::class.java)

        verify(roleRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteRole() {
        val originalEntity = RoleTestData.createRoleEntity()

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(originalEntity))

        roleService.delete(ID)

        verify(oidcAdminService).deleteRole(NAME)
        verify(roleRepository).deleteById(ID)
    }

    @Test
    fun shouldAddPermission() {
        val originalEntity = RoleTestData.createRoleEntity().copy(permissions = emptySet())
        val savedEntity = RoleTestData.createRoleEntity(MemberTestData.createMemberEntity())

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(originalEntity))

        roleService.addPermission(ID, Permission.MANAGE_MEMBERS)

        verify(oidcAdminService).addPermission(NAME, Permission.MANAGE_MEMBERS)
        verify(roleRepository).save(savedEntity)
    }

    @Test
    fun shouldThrowExceptionWithUnknownRoleOnAddPermission() {
        `when`(roleRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { roleService.addPermission(ID, Permission.MANAGE_MEMBERS) }
            .isInstanceOf(RoleNotFoundException::class.java)

        verifyNoInteractions(oidcAdminService)
        verify(roleRepository, never()).save(any())
    }

    @Test
    fun shouldRemovePermission() {
        val originalEntity = RoleTestData.createRoleEntity()
        val savedEntity = RoleTestData.createRoleEntity().copy(permissions = emptySet())

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(originalEntity))

        roleService.removePermission(ID, Permission.MANAGE_MEMBERS)

        verify(oidcAdminService).removePermission(NAME, Permission.MANAGE_MEMBERS)
        verify(roleRepository).save(savedEntity)
    }

    @Test
    fun shouldThrowExceptionWithUnknownRoleOnRemovePermission() {
        `when`(roleRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { roleService.removePermission(ID, Permission.MANAGE_MEMBERS) }
            .isInstanceOf(RoleNotFoundException::class.java)

        verifyNoInteractions(oidcAdminService)
        verify(roleRepository, never()).save(any())
    }

    @Test
    fun shouldSetHolder() {
        val originalEntity = RoleTestData.createRoleEntity()
        val memberEntity = MemberTestData.createMemberEntity()
        val holder = MemberTestData.createMemberId()
        val savedEntity = RoleTestData.createRoleEntity(MemberTestData.createMemberId().toMemberEntity())

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(originalEntity))
        `when`(memberRepository.findById(MemberTestData.ID)).thenReturn(Optional.of(memberEntity))

        roleService.setHolder(ID, holder)

        verify(oidcAdminService).assignRoleExclusivelyToUser(Subject(SUBJECT), NAME)
        verify(roleRepository).save(savedEntity)
        verify(electionService).elect(RoleTestData.createRoleId(), holder)
    }

    @Test
    fun shouldThrowExceptionWithUnknownRoleOnSetHolder() {
        val holder = MemberTestData.createMemberId()

        `when`(roleRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { roleService.setHolder(ID, holder) }
            .isInstanceOf(RoleNotFoundException::class.java)

        verifyNoInteractions(oidcAdminService)
        verify(roleRepository, never()).save(any())
        verifyNoInteractions(electionService)
    }

    @Test
    fun shouldThrowExceptionWithUnknownMemberOnSetHolder() {
        val originalEntity = RoleTestData.createRoleEntity()
        val holder = MemberTestData.createMemberId()

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(originalEntity))
        `when`(memberRepository.findById(MemberTestData.ID)).thenReturn(Optional.empty())

        assertThatThrownBy { roleService.setHolder(ID, holder) }
            .isInstanceOf(MemberNotFoundException::class.java)

        verifyNoInteractions(oidcAdminService)
        verify(roleRepository, never()).save(any())
        verifyNoInteractions(electionService)
    }

    @Test
    fun shouldRemoveHolder() {
        val originalEntity = RoleTestData.createRoleEntity()
        val savedEntity = RoleTestData.createRoleEntity().copy(holder = null)

        `when`(roleRepository.findById(ID)).thenReturn(Optional.of(originalEntity))

        roleService.removeHolder(ID)

        verify(oidcAdminService).unassignExclusiveRole(NAME)
        verify(roleRepository).save(savedEntity)
        verify(electionService).finish(RoleTestData.createRoleId())
    }

    @Test
    fun shouldThrowExceptionWithUnknownRoleOnRemoveHolder() {
        `when`(roleRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { roleService.removeHolder(ID) }
            .isInstanceOf(RoleNotFoundException::class.java)

        verifyNoInteractions(oidcAdminService)
        verify(roleRepository, never()).save(any())
        verifyNoInteractions(electionService)
    }
}
