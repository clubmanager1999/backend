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
package com.github.clubmanager1999.backend.domain.donor

import com.github.clubmanager1999.backend.domain.donor.DonorTestData.ID
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
    @Mock lateinit var donorRepository: DonorRepository

    @InjectMocks lateinit var donorService: DonorService

    @Test
    fun shouldGetDonorById() {
        val existingDonor = DonorTestData.createExistingDonor()
        val savedEntity = DonorTestData.createDonorEntity()

        `when`(donorRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        assertThat(donorService.get(ID)).isEqualTo(existingDonor)
    }

    @Test
    fun shouldThrowExceptionIfDonorIsNotFoundById() {
        `when`(donorRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { donorService.get(ID) }
            .isInstanceOf(DonorNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllDonors() {
        val existingDonor = DonorTestData.createExistingDonor()
        val savedEntity = DonorTestData.createDonorEntity()

        `when`(donorRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(donorService.getAll()).containsExactly(existingDonor)
    }

    @Test
    fun shouldCreateDonor() {
        val newDonor = DonorTestData.createNewDonor()
        val savedEntity = DonorTestData.createDonorEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(donorRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(donorService.create(newDonor)).isEqualTo(DonorTestData.createDonorId())
    }

    @Test
    fun shouldUpdateDonor() {
        val newDonor = DonorTestData.createNewDonor()
        val savedEntity = DonorTestData.createDonorEntity()

        `when`(donorRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(donorRepository.save(savedEntity)).thenReturn(savedEntity)

        assertThat(donorService.update(ID, newDonor)).isEqualTo(DonorTestData.createDonorId())
    }

    @Test
    fun shouldThrowExceptionIfUpdateDonorIsNotFound() {
        val newDonor = DonorTestData.createNewDonor()
        `when`(donorRepository.findById(ID)).thenReturn(Optional.empty())

        assertThatThrownBy { donorService.update(ID, newDonor) }
            .isInstanceOf(DonorNotFoundException::class.java)

        verify(donorRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteDonor() {
        donorService.delete(ID)

        verify(donorRepository).deleteById(ID)
    }
}
