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

import org.springframework.stereotype.Service

@Service
class TemplateService(
    val templateRepository: TemplateRepository,
) {
    fun get(id: Long): ExistingTemplate {
        return templateRepository
            .findById(id)
            .map { it.toExistingTemplate() }
            .orElseThrow { TemplateNotFoundException(id) }
    }

    fun getAll(): List<ExistingTemplate> {
        return templateRepository.findAll().map { it.toExistingTemplate() }
    }

    fun create(newTemplate: NewTemplate): TemplateId {
        return newTemplate
            .toTemplateEntity(null)
            .let { templateRepository.save(it) }
            .toTemplateId()
    }

    fun update(
        id: Long,
        newTemplate: NewTemplate,
    ): TemplateId {
        return templateRepository
            .findById(id)
            .orElseThrow { TemplateNotFoundException(id) }
            .let { newTemplate.toTemplateEntity(id) }
            .let { templateRepository.save(it) }
            .toTemplateId()
    }

    fun delete(id: Long) {
        templateRepository.deleteById(id)
    }
}
