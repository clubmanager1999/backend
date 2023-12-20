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
package com.github.clubmanager1999.backend.transaction.mapping

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
class MappingController(val mappingService: MappingService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/mappings/{id}").build()

    @GetMapping("/api/mappings/{id}")
    fun getMapping(
        @PathVariable id: Long,
    ): ExistingMapping {
        return mappingService.get(id)
    }

    @GetMapping("/api/mappings")
    fun getAllMappings(): List<ExistingMapping> {
        return mappingService.getAll()
    }

    @PostMapping("/api/mappings")
    fun createMapping(
        @RequestBody @Valid newMapping: NewMapping,
    ): ResponseEntity<Void> {
        val mappingId = mappingService.create(newMapping)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(mappingId.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/mappings/{id}")
    fun updateMapping(
        @PathVariable id: Long,
        @RequestBody @Valid newMapping: NewMapping,
    ): ResponseEntity<Void> {
        mappingService.update(id, newMapping)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/mappings/{id}")
    fun deleteMapping(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        mappingService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
