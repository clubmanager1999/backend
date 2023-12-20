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
package com.github.clubmanager1999.backend.donor

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
class DonorController(val donorService: DonorService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/donors/{id}").build()

    @GetMapping("/api/donors/{id}")
    fun getDonor(
        @PathVariable id: Long,
    ): ExistingDonor {
        return donorService.get(id)
    }

    @GetMapping("/api/donors")
    fun getAllDonors(): List<ExistingDonor> {
        return donorService.getAll()
    }

    @PostMapping("/api/donors")
    fun createDonor(
        @RequestBody @Valid newDonor: NewDonor,
    ): ResponseEntity<Void> {
        val donorId = donorService.create(newDonor)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(donorId.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/donors/{id}")
    fun updateDonor(
        @PathVariable id: Long,
        @RequestBody @Valid newDonor: NewDonor,
    ): ResponseEntity<Void> {
        donorService.update(id, newDonor)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/donors/{id}")
    fun deleteDonor(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        donorService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
