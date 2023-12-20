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
import com.github.clubmanager1999.backend.security.Permission
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

@RestController
class RoleController(val roleService: RoleService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/roles/{id}").build()

    @GetMapping("/api/roles/{id}")
    fun getRole(
        @PathVariable id: Long,
    ): ExistingRole {
        return roleService.get(id)
    }

    @GetMapping("/api/roles")
    fun getAllRoles(): List<ExistingRole> {
        return roleService.getAll()
    }

    @PostMapping("/api/roles")
    fun createRole(
        @RequestBody @Valid newRole: NewRole,
    ): ResponseEntity<Void> {
        val roleId = roleService.create(newRole)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(roleId.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/roles/{id}")
    fun updateRole(
        @PathVariable id: Long,
        @RequestBody @Valid newRole: NewRole,
    ): ResponseEntity<Void> {
        roleService.update(id, newRole)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/roles/{id}")
    fun deleteRole(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        roleService.delete(id)

        return ResponseEntity.noContent().build()
    }

    @PutMapping("/api/roles/{id}/permissions")
    fun addPermission(
        @PathVariable id: Long,
        @RequestBody @Valid permission: NewPermission,
    ): ResponseEntity<Void> {
        roleService.addPermission(id, permission.permission)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/roles/{id}/permissions/{permission}")
    fun removePermission(
        @PathVariable id: Long,
        @PathVariable permission: Permission,
    ): ResponseEntity<Void> {
        roleService.removePermission(id, permission)

        return ResponseEntity.noContent().build()
    }

    @PutMapping("/api/roles/{id}/holder")
    fun setMember(
        @PathVariable id: Long,
        @RequestBody @Valid holder: MemberId,
    ): ResponseEntity<Void> {
        roleService.setHolder(id, holder)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/roles/{id}/holder")
    fun removeMember(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        roleService.removeHolder(id)

        return ResponseEntity.noContent().build()
    }
}
