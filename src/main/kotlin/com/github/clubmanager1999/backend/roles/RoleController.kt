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
package com.github.clubmanager1999.backend.roles

import com.github.clubmanager1999.backend.oidc.OidcAdminService
import com.github.clubmanager1999.backend.oidc.OidcRole
import com.github.clubmanager1999.backend.security.Permission
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

@RestController
class RoleController(val oidcAdminService: OidcAdminService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/roles/{id}").build()

    @GetMapping("/api/roles/{name}")
    fun getRole(
        @PathVariable name: String,
    ): OidcRole {
        return oidcAdminService.getRole(name)
    }

    @GetMapping("/api/roles")
    fun getAllRoles(): List<OidcRole> {
        return oidcAdminService.getRoles()
    }

    @PostMapping("/api/roles")
    fun createRole(
        @RequestBody newRole: NewRole,
    ): ResponseEntity<Void> {
        oidcAdminService.createRole(newRole.name)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(newRole.name)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PostMapping("/api/roles/{name}/permissions")
    fun addPermission(
        @PathVariable name: String,
        @RequestBody newPermission: NewPermission,
    ): ResponseEntity<Void> {
        oidcAdminService.addPermission(name, newPermission.permission)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/roles/{name}/permissions/{permission}")
    fun removePermission(
        @PathVariable name: String,
        @PathVariable permission: Permission,
    ): ResponseEntity<Void> {
        oidcAdminService.removePermission(name, permission)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/roles/{name}")
    fun deleteRole(
        @PathVariable name: String,
    ): ResponseEntity<Void> {
        oidcAdminService.deleteRole(name)

        return ResponseEntity.noContent().build()
    }
}
