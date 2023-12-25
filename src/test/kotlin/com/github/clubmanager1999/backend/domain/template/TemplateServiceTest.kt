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

import com.github.clubmanager1999.backend.domain.template.TemplateTestData.ID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class TemplateServiceTest {
    @Mock lateinit var templateRepository: TemplateRepository

    @InjectMocks lateinit var templateService: TemplateService

    @Test
    fun shouldGetTemplateById() {
        val existingTemplate = TemplateTestData.createExistingTemplate()
        val savedEntity = TemplateTestData.createTemplateEntity()

        `when`(templateRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        assertThat(templateService.get(ID)).isEqualTo(existingTemplate)
    }

    @Test
    fun shouldThrowExceptionIfTemplateIsNotFoundById() {
        `when`(templateRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { templateService.get(ID) }
            .isInstanceOf(TemplateNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllTemplates() {
        val existingTemplate = TemplateTestData.createExistingTemplate()
        val savedEntity = TemplateTestData.createTemplateEntity()

        `when`(templateRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(templateService.getAll()).containsExactly(existingTemplate)
    }

    @Test
    fun shouldCreateTemplate() {
        val newTemplate = TemplateTestData.createNewTemplate()
        val savedEntity = TemplateTestData.createTemplateEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(templateRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(templateService.create(newTemplate)).isEqualTo(TemplateTestData.createTemplateId())
    }

    @Test
    fun shouldUpdateTemplate() {
        val newTemplate = TemplateTestData.createNewTemplate()
        val savedEntity = TemplateTestData.createTemplateEntity()

        `when`(templateRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(templateRepository.save(savedEntity)).thenReturn(savedEntity)

        assertThat(templateService.update(ID, newTemplate)).isEqualTo(TemplateTestData.createTemplateId())
    }

    @Test
    fun shouldThrowExceptionIfUpdateTemplateIsNotFound() {
        val newTemplate = TemplateTestData.createNewTemplate()
        `when`(templateRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { templateService.update(ID, newTemplate) }
            .isInstanceOf(TemplateNotFoundException::class.java)

        verify(templateRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteTemplate() {
        templateService.delete(ID)

        verify(templateRepository).deleteById(ID)
    }
}
