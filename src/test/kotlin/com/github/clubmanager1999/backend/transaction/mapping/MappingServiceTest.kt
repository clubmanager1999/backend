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
    @Mock lateinit var mappingEntityMapper: MappingEntityMapper

    @Mock lateinit var mappingRepository: MappingRepository

    @InjectMocks lateinit var mappingService: MappingService

    @Test
    fun shouldGetMappingById() {
        val existingMapping = MappingTestData.createExistingMapping()
        val savedEntity = MappingTestData.createMappingEntity()

        `when`(mappingEntityMapper.toExistingMapping(savedEntity)).thenReturn(existingMapping)

        `when`(mappingRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(mappingService.get(42)).isEqualTo(existingMapping)
    }

    @Test
    fun shouldThrowExceptionIfMappingIsNotFoundById() {
        `when`(mappingRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { mappingService.get(42) }
            .isInstanceOf(MappingNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllMappings() {
        val existingMapping = MappingTestData.createExistingMapping()
        val savedEntity = MappingTestData.createMappingEntity()

        `when`(mappingRepository.findAll()).thenReturn(listOf(savedEntity))

        `when`(mappingEntityMapper.toExistingMapping(savedEntity)).thenReturn(existingMapping)

        assertThat(mappingService.getAll()).containsExactly(existingMapping)
    }

    @Test
    fun shouldCreateMapping() {
        val newMapping = MappingTestData.createNewMapping()
        val existingMapping = MappingTestData.createExistingMapping()
        val savedEntity = MappingTestData.createMappingEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(mappingEntityMapper.toMappingEntity(null, newMapping)).thenReturn(newEntity)

        `when`(mappingRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(mappingEntityMapper.toExistingMapping(savedEntity)).thenReturn(existingMapping)

        assertThat(mappingService.create(newMapping)).isEqualTo(existingMapping)
    }

    @Test
    fun shouldUpdateMapping() {
        val newMapping = MappingTestData.createNewMapping()
        val existingMapping = MappingTestData.createExistingMapping()
        val savedEntity = MappingTestData.createMappingEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(mappingRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(mappingEntityMapper.toMappingEntity(42, newMapping)).thenReturn(newEntity)

        `when`(mappingRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(mappingEntityMapper.toExistingMapping(savedEntity)).thenReturn(existingMapping)

        assertThat(mappingService.update(42, newMapping)).isEqualTo(existingMapping)
    }

    @Test
    fun shouldThrowExceptionIfUpdateMappingIsNotFound() {
        val newMapping = MappingTestData.createNewMapping()
        `when`(mappingRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { mappingService.update(42, newMapping) }
            .isInstanceOf(MappingNotFoundException::class.java)

        verify(mappingRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteMapping() {
        mappingService.delete(42)

        verify(mappingRepository).deleteById(42)
    }
}
