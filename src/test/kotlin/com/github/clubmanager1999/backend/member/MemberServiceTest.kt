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
package com.github.clubmanager1999.backend.member

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class MemberServiceTest {
    @Mock lateinit var memberEntityMapper: MemberEntityMapper

    @Mock lateinit var memberRepository: MemberRepository

    @InjectMocks lateinit var memberService: MemberService

    @Test
    fun shouldGetMemberById() {
        val existingMember = MemberTestData.createExistingMember()
        val savedEntity = MemberTestData.createMemberEntity()

        `when`(memberEntityMapper.toExistingMember(savedEntity)).thenReturn(existingMember)

        `when`(memberRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        assertThat(memberService.get(42)).isEqualTo(existingMember)
    }

    @Test
    fun shouldThrowExceptionIfMemberIsNotFoundById() {
        `when`(memberRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { memberService.get(42) }.isInstanceOf(MemberNotFoundException::class.java)
    }

    @Test
    fun shouldGetAllMembers() {
        val existingMember = MemberTestData.createExistingMember()
        val savedEntity = MemberTestData.createMemberEntity()

        `when`(memberRepository.findAll()).thenReturn(listOf(savedEntity))

        `when`(memberEntityMapper.toExistingMember(savedEntity)).thenReturn(existingMember)

        assertThat(memberService.getAll()).containsExactly(existingMember)
    }

    @Test
    fun shouldCreateMember() {
        val newMember = MemberTestData.createNewMember()
        val existingMember = MemberTestData.createExistingMember()
        val savedEntity = MemberTestData.createMemberEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(
            memberEntityMapper.toMemberEntity(
                null,
                newMember,
            ),
        )
            .thenReturn(newEntity)

        `when`(memberRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(memberEntityMapper.toExistingMember(savedEntity)).thenReturn(existingMember)

        assertThat(memberService.create(newMember)).isEqualTo(existingMember)
    }

    @Test
    fun shouldUpdateMember() {
        val newMember = MemberTestData.createNewMember()
        val existingMember = MemberTestData.createExistingMember()
        val savedEntity = MemberTestData.createMemberEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(memberRepository.findById(42)).thenReturn(Optional.of(savedEntity))

        `when`(
            memberEntityMapper.toMemberEntity(
                42,
                newMember,
            ),
        )
            .thenReturn(newEntity)

        `when`(memberRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(memberEntityMapper.toExistingMember(savedEntity)).thenReturn(existingMember)

        assertThat(memberService.update(42, newMember)).isEqualTo(existingMember)
    }

    @Test
    fun shouldThrowExceptionIfUpdateMemberIsNotFound() {
        val newMember = MemberTestData.createNewMember()
        `when`(memberRepository.findById(42)).thenReturn(Optional.empty())

        assertThatThrownBy { memberService.update(42, newMember) }
            .isInstanceOf(MemberNotFoundException::class.java)

        verify(memberRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteMember() {
        memberService.delete(42)

        verify(memberRepository).deleteById(42)
    }
}
