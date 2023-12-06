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
package com.github.clubmanager1999.backend.transaction.area

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
class AreaController(val areaService: AreaService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/areas/{id}").build()

    @GetMapping("/api/areas/{id}")
    fun getArea(
        @PathVariable id: Long,
    ): ExistingArea {
        return areaService.get(id)
    }

    @GetMapping("/api/areas")
    fun getAllAreas(): List<ExistingArea> {
        return areaService.getAll()
    }

    @PostMapping("/api/areas")
    fun createArea(
        @RequestBody @Valid newArea: NewArea,
    ): ResponseEntity<Void> {
        val existingArea = areaService.create(newArea)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(existingArea.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/areas/{id}")
    fun updateArea(
        @PathVariable id: Long,
        @RequestBody @Valid newArea: NewArea,
    ): ResponseEntity<Void> {
        areaService.update(id, newArea)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/areas/{id}")
    fun deleteArea(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        areaService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
