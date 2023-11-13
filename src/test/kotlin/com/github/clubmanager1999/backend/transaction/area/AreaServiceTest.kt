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

import com.github.clubmanager1999.backend.transaction.area.AreaTestData.ID
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
class AreaServiceTest {
    @Mock lateinit var areaRepository: AreaRepository

    @InjectMocks lateinit var areaService: AreaService

    @Test
    fun shouldGetAreaById() {
        val existingArea = AreaTestData.createExistingArea()
        val savedEntity = AreaTestData.createAreaEntity()

        `when`(areaRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        assertThat(areaService.get(ID)).isEqualTo(existingArea)
    }

    @Test
    fun shouldThrowExceptionIfAreaIsNotFoundById() {
        `when`(areaRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { areaService.get(ID) }
            .isInstanceOf(AreaNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllAreas() {
        val existingArea = AreaTestData.createExistingArea()
        val savedEntity = AreaTestData.createAreaEntity()

        `when`(areaRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(areaService.getAll()).containsExactly(existingArea)
    }

    @Test
    fun shouldCreateArea() {
        val newArea = AreaTestData.createNewArea()
        val existingArea = AreaTestData.createExistingArea()
        val savedEntity = AreaTestData.createAreaEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(areaRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(areaService.create(newArea)).isEqualTo(existingArea)
    }

    @Test
    fun shouldUpdateArea() {
        val newArea = AreaTestData.createNewArea()
        val existingArea = AreaTestData.createExistingArea()
        val savedEntity = AreaTestData.createAreaEntity()

        `when`(areaRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(areaRepository.save(savedEntity)).thenReturn(savedEntity)

        assertThat(areaService.update(ID, newArea)).isEqualTo(existingArea)
    }

    @Test
    fun shouldThrowExceptionIfUpdateAreaIsNotFound() {
        val newArea = AreaTestData.createNewArea()
        `when`(areaRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { areaService.update(ID, newArea) }
            .isInstanceOf(AreaNotFoundException::class.java)

        verify(areaRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteArea() {
        areaService.delete(ID)

        verify(areaRepository).deleteById(ID)
    }
}
