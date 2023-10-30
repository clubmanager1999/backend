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

import com.github.clubmanager1999.backend.oidc.OidcAdminService
import com.github.clubmanager1999.backend.oidc.OidcTestData
import com.github.clubmanager1999.backend.oidc.OidcUserMapper
import com.github.clubmanager1999.backend.oidc.Subject
import com.github.clubmanager1999.backend.security.SecurityTestData.SUBJECT
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class MemberServiceTest {
    @Mock lateinit var memberEntityMapper: MemberEntityMapper

    @Mock lateinit var memberRepository: MemberRepository

    @Mock lateinit var oidcUserMapper: OidcUserMapper

    @Mock lateinit var oidcAdminService: OidcAdminService

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
                SUBJECT,
                newMember,
            ),
        )
            .thenReturn(newEntity)

        `when`(memberRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(memberEntityMapper.toExistingMember(savedEntity)).thenReturn(existingMember)

        `when`(oidcUserMapper.toOidcUser(newMember)).thenReturn(OidcTestData.createOidcUser())

        `when`(oidcAdminService.createUser(any())).thenReturn(Subject(SUBJECT))

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
                SUBJECT,
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

    @Test
    fun shouldCreateOidcUser() {
        val newMember = MemberTestData.createNewMember()
        val existingMember = MemberTestData.createExistingMember()
        val savedEntity = MemberTestData.createMemberEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(
            memberEntityMapper.toMemberEntity(
                null,
                SUBJECT,
                newMember,
            ),
        )
            .thenReturn(newEntity)

        `when`(memberRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(memberEntityMapper.toExistingMember(savedEntity)).thenReturn(existingMember)

        `when`(oidcUserMapper.toOidcUser(newMember)).thenReturn(OidcTestData.createOidcUser())

        `when`(oidcAdminService.createUser(any())).thenReturn(Subject(SUBJECT))

        memberService.create(newMember)

        verify(oidcAdminService).createUser(OidcTestData.createOidcUser())
    }

    @Test
    fun shouldNotCreateMemberIfOidcFails() {
        val newMember = MemberTestData.createNewMember()
        val exception = RuntimeException()

        `when`(oidcUserMapper.toOidcUser(newMember)).thenReturn(OidcTestData.createOidcUser())

        `when`(oidcAdminService.createUser(any())).thenThrow(exception)

        assertThatThrownBy { memberService.create(newMember) }
            .isInstanceOf(RuntimeException::class.java)

        verify(oidcAdminService).createUser(OidcTestData.createOidcUser())
        verifyNoInteractions(memberRepository)
    }

    @Test
    fun shouldUpdateOidcUser() {
        val newMember = MemberTestData.createNewMember()
        val existingMember = MemberTestData.createExistingMember()
        val savedEntity = MemberTestData.createMemberEntity()
        val newEntity = savedEntity.copy(id = null)

        `when`(oidcUserMapper.toOidcUser(newMember)).thenReturn(OidcTestData.createOidcUser())

        `when`(memberRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(
            memberEntityMapper.toMemberEntity(
                ID,
                SUBJECT,
                newMember,
            ),
        )
            .thenReturn(newEntity)

        `when`(memberRepository.save(newEntity)).thenReturn(savedEntity)

        `when`(memberEntityMapper.toExistingMember(savedEntity)).thenReturn(existingMember)

        memberService.update(ID, newMember)

        verify(oidcAdminService).updateUser(Subject(SUBJECT), OidcTestData.createOidcUser())
    }

    @Test
    fun shouldNotUpdateMemberIfOidcFails() {
        val newMember = MemberTestData.createNewMember()
        val savedEntity = MemberTestData.createMemberEntity()
        val exception = RuntimeException()

        `when`(oidcUserMapper.toOidcUser(newMember)).thenReturn(OidcTestData.createOidcUser())

        `when`(memberRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(oidcAdminService.updateUser(any(), any())).thenThrow(exception)

        assertThatThrownBy { memberService.update(ID, newMember) }
            .isInstanceOf(RuntimeException::class.java)

        verify(oidcAdminService).updateUser(Subject(SUBJECT), OidcTestData.createOidcUser())
        verify(memberRepository, never()).save(any())
    }

    @Test
    fun shouldDeleteOidcUser() {
        val savedEntity = MemberTestData.createMemberEntity()

        `when`(memberRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        memberService.delete(ID)

        verify(oidcAdminService).deleteUser(Subject(SUBJECT))
    }

    @Test
    fun shouldNotDeleteMemberIfOidcFails() {
        val savedEntity = MemberTestData.createMemberEntity()
        val exception = RuntimeException()

        `when`(memberRepository.findById(ID)).thenReturn(Optional.of(savedEntity))

        `when`(oidcAdminService.deleteUser(any())).thenThrow(exception)

        assertThatThrownBy { memberService.delete(ID) }
            .isInstanceOf(RuntimeException::class.java)

        verify(oidcAdminService).deleteUser(Subject(SUBJECT))
        verify(memberRepository, never()).save(any())
    }
}
