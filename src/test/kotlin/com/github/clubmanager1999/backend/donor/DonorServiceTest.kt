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
class DonorServiceTest {
    @Mock lateinit var donorEntityMapper: DonorEntityMapper

    @Mock lateinit var donorRepository: DonorRepository

    @InjectMocks lateinit var donorService: DonorService

    @Test
    fun shouldGetDonorById() {
        val existingDonor = DonorTestData.createExistingDonor()
        val savedEntity = DonorTestData.createDonorEntity()

        `when`(donorEntityMapper.toExistingDonor(savedEntity)).thenReturn(existingDonor)

        `when`(donorRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(donorService.get(42)).isEqualTo(existingDonor)
    }

    @Test
    fun shouldThrowExceptionIfDonorIsNotFoundById() {
        `when`(donorRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { donorService.get(42) }
            .isInstanceOf(DonorNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllDonors() {
        val existingDonor = DonorTestData.createExistingDonor()
        val savedEntity = DonorTestData.createDonorEntity()

        `when`(donorRepository.findAll()).thenReturn(listOf(savedEntity))

        `when`(donorEntityMapper.toExistingDonor(savedEntity)).thenReturn(existingDonor)

        assertThat(donorService.getAll()).containsExactly(existingDonor)
    }

    @Test
    fun shouldCreateDonor() {
        val newDonor = DonorTestData.createNewDonor()
        val existingDonor = DonorTestData.createExistingDonor()
        val savedEntity = DonorTestData.createDonorEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(donorEntityMapper.toDonorEntity(null, newDonor)).thenReturn(newEntity)

        `when`(donorRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(donorEntityMapper.toExistingDonor(savedEntity)).thenReturn(existingDonor)

        assertThat(donorService.create(newDonor)).isEqualTo(existingDonor)
    }

    @Test
    fun shouldUpdateDonor() {
        val newDonor = DonorTestData.createNewDonor()
        val existingDonor = DonorTestData.createExistingDonor()
        val savedEntity = DonorTestData.createDonorEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(donorRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(donorEntityMapper.toDonorEntity(42, newDonor)).thenReturn(newEntity)

        `when`(donorRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(donorEntityMapper.toExistingDonor(savedEntity)).thenReturn(existingDonor)

        assertThat(donorService.update(42, newDonor)).isEqualTo(existingDonor)
    }

    @Test
    fun shouldThrowExceptionIfUpdateDonorIsNotFound() {
        val newDonor = DonorTestData.createNewDonor()
        `when`(donorRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { donorService.update(42, newDonor) }
            .isInstanceOf(DonorNotFoundException::class.java)

        verify(donorRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteDonor() {
        donorService.delete(42)

        verify(donorRepository).deleteById(42)
    }
}
