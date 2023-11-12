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
package com.github.clubmanager1999.backend.creditor

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
class CreditorServiceTest {
    @Mock lateinit var creditorRepository: CreditorRepository

    @InjectMocks lateinit var creditorService: CreditorService

    @Test
    fun shouldGetCreditorById() {
        val existingCreditor = CreditorTestData.createExistingCreditor()
        val savedEntity = CreditorTestData.createCreditorEntity()

        `when`(creditorRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(creditorService.get(42)).isEqualTo(existingCreditor)
    }

    @Test
    fun shouldThrowExceptionIfCreditorIsNotFoundById() {
        `when`(creditorRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { creditorService.get(42) }
            .isInstanceOf(CreditorNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllCreditors() {
        val existingCreditor = CreditorTestData.createExistingCreditor()
        val savedEntity = CreditorTestData.createCreditorEntity()

        `when`(creditorRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(creditorService.getAll()).containsExactly(existingCreditor)
    }

    @Test
    fun shouldCreateCreditor() {
        val newCreditor = CreditorTestData.createNewCreditor()
        val existingCreditor = CreditorTestData.createExistingCreditor()
        val savedEntity = CreditorTestData.createCreditorEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(creditorRepository.save(newEntity)).thenReturn(savedEntity)

        assertThat(creditorService.create(newCreditor)).isEqualTo(existingCreditor)
    }

    @Test
    fun shouldUpdateCreditor() {
        val newCreditor = CreditorTestData.createNewCreditor()
        val existingCreditor = CreditorTestData.createExistingCreditor()
        val savedEntity = CreditorTestData.createCreditorEntity()

        `when`(creditorRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(creditorRepository.save(savedEntity)).thenReturn(savedEntity)

        assertThat(creditorService.update(42, newCreditor)).isEqualTo(existingCreditor)
    }

    @Test
    fun shouldThrowExceptionIfUpdateCreditorIsNotFound() {
        val newCreditor = CreditorTestData.createNewCreditor()
        `when`(creditorRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { creditorService.update(42, newCreditor) }
            .isInstanceOf(CreditorNotFoundException::class.java)

        verify(creditorRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteCreditor() {
        creditorService.delete(42)

        verify(creditorRepository).deleteById(42)
    }
}
