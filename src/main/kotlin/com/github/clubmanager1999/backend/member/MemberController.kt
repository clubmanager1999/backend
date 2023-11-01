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
class MemberController(val memberService: MemberService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance()

    @GetMapping("/api/members/{id}")
    fun getMember(
        @PathVariable id: Long,
    ): ExistingMember {
        return memberService.get(id)
    }

    @GetMapping("/api/members")
    fun getAllMembers(): List<ExistingMember> {
        return memberService.getAll()
    }

    @PostMapping("/api/members")
    fun createMember(
        @RequestBody newMember: NewMember,
    ): ResponseEntity<Void> {
        val existingMember = memberService.create(newMember)

        val uriComponents: UriComponents = uriComponentsBuilder.path("/api/members/{id}").buildAndExpand(existingMember.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/members/{id}")
    fun updateMember(
        @PathVariable id: Long,
        @RequestBody newMember: NewMember,
    ): ResponseEntity<Void> {
        memberService.update(id, newMember)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/members/{id}")
    fun deleteMember(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        memberService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
