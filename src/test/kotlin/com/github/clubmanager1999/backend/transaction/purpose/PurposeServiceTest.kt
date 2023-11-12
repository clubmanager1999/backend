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
package com.github.clubmanager1999.backend.transaction.purpose

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
class PurposeServiceTest {
    @Mock lateinit var purposeEntityMapper: PurposeEntityMapper

    @Mock lateinit var purposeRepository: PurposeRepository

    @InjectMocks lateinit var purposeService: PurposeService

    @Test
    fun shouldGetPurposeById() {
        val existingPurpose = PurposeTestData.createExistingPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()

        `when`(purposeEntityMapper.toExistingPurpose(savedEntity)).thenReturn(existingPurpose)

        `when`(purposeRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(purposeService.get(42)).isEqualTo(existingPurpose)
    }

    @Test
    fun shouldThrowExceptionIfPurposeIsNotFoundById() {
        `when`(purposeRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { purposeService.get(42) }
            .isInstanceOf(PurposeNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllPurposes() {
        val existingPurpose = PurposeTestData.createExistingPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()

        `when`(purposeRepository.findAll()).thenReturn(listOf(savedEntity))

        `when`(purposeEntityMapper.toExistingPurpose(savedEntity)).thenReturn(existingPurpose)

        assertThat(purposeService.getAll()).containsExactly(existingPurpose)
    }

    @Test
    fun shouldCreatePurpose() {
        val newPurpose = PurposeTestData.createNewPurpose()
        val existingPurpose = PurposeTestData.createExistingPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(purposeEntityMapper.toPurposeEntity(null, newPurpose)).thenReturn(newEntity)

        `when`(purposeRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(purposeEntityMapper.toExistingPurpose(savedEntity)).thenReturn(existingPurpose)

        assertThat(purposeService.create(newPurpose)).isEqualTo(existingPurpose)
    }

    @Test
    fun shouldUpdatePurpose() {
        val newPurpose = PurposeTestData.createNewPurpose()
        val existingPurpose = PurposeTestData.createExistingPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(purposeRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(purposeEntityMapper.toPurposeEntity(42, newPurpose)).thenReturn(newEntity)

        `when`(purposeRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(purposeEntityMapper.toExistingPurpose(savedEntity)).thenReturn(existingPurpose)

        assertThat(purposeService.update(42, newPurpose)).isEqualTo(existingPurpose)
    }

    @Test
    fun shouldThrowExceptionIfUpdatePurposeIsNotFound() {
        val newPurpose = PurposeTestData.createNewPurpose()
        `when`(purposeRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { purposeService.update(42, newPurpose) }
            .isInstanceOf(PurposeNotFoundException::class.java)

        verify(purposeRepository, never()).save(any())
    }

    @Test
    fun shouldDeletePurpose() {
        purposeService.delete(42)

        verify(purposeRepository).deleteById(42)
    }
}
