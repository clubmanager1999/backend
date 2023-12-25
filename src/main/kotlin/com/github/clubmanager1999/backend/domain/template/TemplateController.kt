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
package com.github.clubmanager1999.backend.domain.template

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
class TemplateController(val templateService: TemplateService) {
    val uriComponentsBuilder = UriComponentsBuilder.newInstance().path("/api/templates/{id}").build()

    @GetMapping("/api/templates/{id}")
    fun getTemplate(
        @PathVariable id: Long,
    ): ExistingTemplate {
        return templateService.get(id)
    }

    @GetMapping("/api/templates")
    fun getAllTemplates(): List<ExistingTemplate> {
        return templateService.getAll()
    }

    @PostMapping("/api/templates")
    fun createTemplate(
        @RequestBody @Valid newTemplate: NewTemplate,
    ): ResponseEntity<Void> {
        val templateId = templateService.create(newTemplate)

        val uriComponents: UriComponents = uriComponentsBuilder.expand(templateId.id)

        return ResponseEntity.created(uriComponents.toUri()).build()
    }

    @PutMapping("/api/templates/{id}")
    fun updateTemplate(
        @PathVariable id: Long,
        @RequestBody @Valid newTemplate: NewTemplate,
    ): ResponseEntity<Void> {
        templateService.update(id, newTemplate)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/templates/{id}")
    fun deleteTemplate(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        templateService.delete(id)

        return ResponseEntity.noContent().build()
    }
}
