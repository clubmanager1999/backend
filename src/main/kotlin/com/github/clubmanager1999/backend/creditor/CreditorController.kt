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
package com.github.clubmanager1999.backend.creditor

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
class CreditorController(val creditorService: CreditorService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/creditors/{id}").build()

    @GetMapping("/api/creditors/{id}")
    fun getCreditor(
        @PathVariable id: Long,
    ): ExistingCreditor {
        return creditorService.get(id)
    }

    @GetMapping("/api/creditors")
    fun getAllCreditors(): List<ExistingCreditor> {
        return creditorService.getAll()
    }

    @PostMapping("/api/creditors")
    fun createCreditor(
        @RequestBody newCreditor: NewCreditor,
    ): ResponseEntity<Void> {
        val existingCreditor = creditorService.create(newCreditor)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(existingCreditor.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/creditors/{id}")
    fun updateCreditor(
        @PathVariable id: Long,
        @RequestBody newCreditor: NewCreditor,
    ): ResponseEntity<Void> {
        creditorService.update(id, newCreditor)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/creditors/{id}")
    fun deleteCreditor(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        creditorService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
