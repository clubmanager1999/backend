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
package com.github.clubmanager1999.backend.domain.purpose

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
class PurposeController(val purposeService: PurposeService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/purposes/{id}").build()

    @GetMapping("/api/purposes/{id}")
    fun getPurpose(
        @PathVariable id: Long,
    ): ExistingPurpose {
        return purposeService.get(id)
    }

    @GetMapping("/api/purposes")
    fun getAllPurposes(): List<ExistingPurpose> {
        return purposeService.getAll()
    }

    @PostMapping("/api/purposes")
    fun createPurpose(
        @RequestBody @Valid newPurpose: NewPurpose,
    ): ResponseEntity<Void> {
        val purposeId = purposeService.create(newPurpose)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(purposeId.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/purposes/{id}")
    fun updatePurpose(
        @PathVariable id: Long,
        @RequestBody @Valid newPurpose: NewPurpose,
    ): ResponseEntity<Void> {
        purposeService.update(id, newPurpose)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/purposes/{id}")
    fun deletePurpose(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        purposeService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
