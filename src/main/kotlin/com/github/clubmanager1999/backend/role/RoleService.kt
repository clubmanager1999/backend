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

import com.github.clubmanager1999.backend.member.MemberId
import com.github.clubmanager1999.backend.member.MemberNotFoundException
import com.github.clubmanager1999.backend.member.MemberRepository
import com.github.clubmanager1999.backend.member.toMemberEntity
import com.github.clubmanager1999.backend.oidc.OidcAdminService
import com.github.clubmanager1999.backend.oidc.Subject
import com.github.clubmanager1999.backend.security.Permission
import org.springframework.stereotype.Service

@Service
class RoleService(
    val roleRepository: RoleRepository,
    val memberRepository: MemberRepository,
    val oidcAdminService: OidcAdminService,
) {
    fun get(id: Long): ExistingRole {
        return roleRepository
            .findById(id)
            .map { it.toExistingRole() }
            .orElseThrow { RoleNotFoundException(id) }
    }

    fun getAll(): List<ExistingRole> {
        return roleRepository.findAll().map { it.toExistingRole() }
    }

    fun create(newRole: NewRole): ExistingRole {
        oidcAdminService.createRole(newRole.name)

        return newRole
            .toRoleEntity(null)
            .let { roleRepository.save(it) }
            .toExistingRole()
    }

    fun update(
        id: Long,
        newRole: NewRole,
    ): ExistingRole {
        return roleRepository
            .findById(id)
            .orElseThrow { RoleNotFoundException(id) }
            .let { newRole.toRoleEntity(id) }
            .let { roleRepository.save(it) }
            .toExistingRole()
    }

    fun delete(id: Long) {
        val role =
            roleRepository
                .findById(id)
                .orElseThrow { RoleNotFoundException(id) }

        oidcAdminService.deleteRole(role.name)
        roleRepository.deleteById(id)
    }

    fun addPermission(
        id: Long,
        permission: Permission,
    ) {
        roleRepository
            .findById(id)
            .orElseThrow { RoleNotFoundException(id) }
            .let {
                oidcAdminService.addPermission(it.name, permission)
                it
            }
            .let { it.copy(permissions = it.permissions + permission) }
            .let { roleRepository.save(it) }
    }

    fun removePermission(
        id: Long,
        permission: Permission,
    ) {
        roleRepository
            .findById(id)
            .orElseThrow { RoleNotFoundException(id) }
            .let {
                oidcAdminService.removePermission(it.name, permission)
                it
            }
            .let { it.copy(permissions = it.permissions - permission) }
            .let { roleRepository.save(it) }
    }

    fun setHolder(
        id: Long,
        holder: MemberId,
    ) {
        val role =
            roleRepository
                .findById(id)
                .orElseThrow { RoleNotFoundException(id) }

        val member =
            memberRepository
                .findById(holder.id)
                .orElseThrow { MemberNotFoundException(id) }

        oidcAdminService.assignRoleExclusivelyToUser(Subject(member.subject), role.name)
        roleRepository.save(role.copy(holder = holder.toMemberEntity()))
    }

    fun removeHolder(id: Long) {
        val role =
            roleRepository
                .findById(id)
                .orElseThrow { RoleNotFoundException(id) }

        oidcAdminService.unassignExclusiveRole(role.name)
        roleRepository.save(role.copy(holder = null))
    }
}
