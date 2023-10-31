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
package com.github.clubmanager1999.backend.membership

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
class MembershipServiceTest {
    @Mock lateinit var membershipEntityMapper: MembershipEntityMapper

    @Mock lateinit var membershipRepository: MembershipRepository

    @InjectMocks lateinit var membershipService: MembershipService

    @Test
    fun shouldGetMembershipById() {
        val existingMembership = MembershipTestData.createExistingMembership()
        val savedEntity = MembershipTestData.createMembershipEntity()

        `when`(membershipEntityMapper.toExistingMembership(savedEntity)).thenReturn(existingMembership)

        `when`(membershipRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(membershipService.get(42)).isEqualTo(existingMembership)
    }

    @Test
    fun shouldThrowExceptionIfMembershipIsNotFoundById() {
        `when`(membershipRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { membershipService.get(42) }
            .isInstanceOf(MembershipNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllMemberships() {
        val existingMembership = MembershipTestData.createExistingMembership()
        val savedEntity = MembershipTestData.createMembershipEntity()

        `when`(membershipRepository.findAll()).thenReturn(listOf(savedEntity))

        `when`(membershipEntityMapper.toExistingMembership(savedEntity)).thenReturn(existingMembership)

        assertThat(membershipService.getAll()).containsExactly(existingMembership)
    }

    @Test
    fun shouldCreateMembership() {
        val newMembership = MembershipTestData.createNewMembership()
        val existingMembership = MembershipTestData.createExistingMembership()
        val savedEntity = MembershipTestData.createMembershipEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(membershipEntityMapper.toMembershipEntity(null, newMembership)).thenReturn(newEntity)

        `when`(membershipRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(membershipEntityMapper.toExistingMembership(savedEntity)).thenReturn(existingMembership)

        assertThat(membershipService.create(newMembership)).isEqualTo(existingMembership)
    }

    @Test
    fun shouldUpdateMembership() {
        val newMembership = MembershipTestData.createNewMembership()
        val existingMembership = MembershipTestData.createExistingMembership()
        val savedEntity = MembershipTestData.createMembershipEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(membershipRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(membershipEntityMapper.toMembershipEntity(42, newMembership)).thenReturn(newEntity)

        `when`(membershipRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(membershipEntityMapper.toExistingMembership(savedEntity)).thenReturn(existingMembership)

        assertThat(membershipService.update(42, newMembership)).isEqualTo(existingMembership)
    }

    @Test
    fun shouldThrowExceptionIfUpdateMembershipIsNotFound() {
        val newMembership = MembershipTestData.createNewMembership()
        `when`(membershipRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { membershipService.update(42, newMembership) }
            .isInstanceOf(MembershipNotFoundException::class.java)

        verify(membershipRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteMembership() {
        membershipService.delete(42)

        verify(membershipRepository).deleteById(42)
    }
}
