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

import com.github.clubmanager1999.backend.transaction.mapping.MappingTestData.ID
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
class MappingServiceTest {
    @Mock lateinit var mappingRepository: MappingRepository

    @InjectMocks lateinit var mappingService: MappingService

    @Test
    fun shouldGetMappingById() {
        val existingMapping = MappingTestData.createExistingMapping()
        val savedEntity = MappingTestData.createMappingEntity()
        `when`(mappingRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        assertThat(mappingService.get(ID)).isEqualTo(existingMapping)
    }

    @Test
    fun shouldThrowExceptionIfMappingIsNotFoundById() {
        `when`(mappingRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { mappingService.get(ID) }
            .isInstanceOf(MappingNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllMappings() {
        val existingMapping = MappingTestData.createExistingMapping()
        val savedEntity = MappingTestData.createMappingEntity()

        `when`(mappingRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(mappingService.getAll()).containsExactly(existingMapping)
    }

    @Test
    fun shouldCreateMapping() {
        val newMapping = MappingTestData.createNewMapping()
        val savedEntity = MappingTestData.createMappingEntity()
        val newEntity = MappingTestData.createFlatMappingEntity().copy(id = null)

        `when`(mappingRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(mappingService.create(newMapping)).isEqualTo(MappingTestData.createMappingId())
    }

    @Test
    fun shouldUpdateMapping() {
        val newMapping = MappingTestData.createNewMapping()
        val savedEntity = MappingTestData.createMappingEntity()
        val updatedEntity = MappingTestData.createFlatMappingEntity()

        `when`(mappingRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(mappingRepository.save(updatedEntity)).thenReturn(savedEntity)

        assertThat(mappingService.update(ID, newMapping)).isEqualTo(MappingTestData.createMappingId())
    }

    @Test
    fun shouldThrowExceptionIfUpdateMappingIsNotFound() {
        val newMapping = MappingTestData.createNewMapping()
        `when`(mappingRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { mappingService.update(ID, newMapping) }
            .isInstanceOf(MappingNotFoundException::class.java)

        verify(mappingRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteMapping() {
        mappingService.delete(ID)

        verify(mappingRepository).deleteById(ID)
    }
}
