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
package com.github.clubmanager1999.backend.domain.purpose

import com.github.clubmanager1999.backend.domain.purpose.PurposeTestData.ID
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
    @Mock lateinit var purposeRepository: PurposeRepository

    @InjectMocks lateinit var purposeService: PurposeService

    @Test
    fun shouldGetPurposeById() {
        val existingPurpose = PurposeTestData.createExistingPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()

        `when`(purposeRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        assertThat(purposeService.get(ID)).isEqualTo(existingPurpose)
    }

    @Test
    fun shouldThrowExceptionIfPurposeIsNotFoundById() {
        `when`(purposeRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { purposeService.get(ID) }
            .isInstanceOf(PurposeNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllPurposes() {
        val existingPurpose = PurposeTestData.createExistingPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()

        `when`(purposeRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(purposeService.getAll()).containsExactly(existingPurpose)
    }

    @Test
    fun shouldCreatePurpose() {
        val newPurpose = PurposeTestData.createNewPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(purposeRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(purposeService.create(newPurpose)).isEqualTo(PurposeTestData.createPurposeId())
    }

    @Test
    fun shouldUpdatePurpose() {
        val newPurpose = PurposeTestData.createNewPurpose()
        val savedEntity = PurposeTestData.createPurposeEntity()

        `when`(purposeRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(purposeRepository.save(savedEntity)).thenReturn(savedEntity)

        assertThat(purposeService.update(ID, newPurpose)).isEqualTo(PurposeTestData.createPurposeId())
    }

    @Test
    fun shouldThrowExceptionIfUpdatePurposeIsNotFound() {
        val newPurpose = PurposeTestData.createNewPurpose()
        `when`(purposeRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { purposeService.update(ID, newPurpose) }
            .isInstanceOf(PurposeNotFoundException::class.java)

        verify(purposeRepository, never()).save(any())
    }

    @Test
    fun shouldDeletePurpose() {
        purposeService.delete(ID)

        verify(purposeRepository).deleteById(ID)
    }
}
