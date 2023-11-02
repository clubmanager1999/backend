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
package com.github.clubmanager1999.backend.membership

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
class MembershipController(val membershipService: MembershipService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/memberships/{id}").build()

    @GetMapping("/api/memberships/{id}")
    fun getMembership(
        @PathVariable id: Long,
    ): ExistingMembership {
        return membershipService.get(id)
    }

    @GetMapping("/api/memberships")
    fun getAllMemberships(): List<ExistingMembership> {
        return membershipService.getAll()
    }

    @PostMapping("/api/memberships")
    fun createMembership(
        @RequestBody newMembership: NewMembership,
    ): ResponseEntity<Void> {
        val existingMembership = membershipService.create(newMembership)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(existingMembership.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/memberships/{id}")
    fun updateMembership(
        @PathVariable id: Long,
        @RequestBody newMembership: NewMembership,
    ): ResponseEntity<Void> {
        membershipService.update(id, newMembership)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/memberships/{id}")
    fun deleteMembership(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        membershipService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
