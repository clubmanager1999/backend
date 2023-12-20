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
package com.github.clubmanager1999.backend.domain.election

import com.github.clubmanager1999.backend.domain.member.MemberTestData
import com.github.clubmanager1999.backend.domain.member.toMemberEntity
import com.github.clubmanager1999.backend.domain.role.RoleTestData
import com.github.clubmanager1999.backend.domain.role.toRoleEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ElectionServiceTest {
    @Mock lateinit var electionRepository: ElectionRepository

    @InjectMocks lateinit var electionService: ElectionService

    @Test
    fun shouldGetAllElections() {
        val existingElection = ElectionTestData.createExistingElection()
        val savedEntity = ElectionTestData.createElectionEntity()

        `when`(electionRepository.findAll()).thenReturn(listOf(savedEntity))

        assertThat(electionService.getAll()).containsExactly(existingElection)
    }

    @Test
    fun shouldElectMember() {
        val newEntity =
            ElectionEntity(
                id = null,
                role = RoleTestData.createRoleId().toRoleEntity(),
                member = MemberTestData.createMemberId().toMemberEntity(),
                validFrom = LocalDate.now(),
                validTo = null,
            )

        `when`(electionRepository.findLatestEntryByRoleId(RoleTestData.createRoleId().id)).thenReturn(Optional.empty())

        electionService.elect(RoleTestData.createRoleId(), MemberTestData.createMemberId())

        verify(electionRepository).save(newEntity)
    }

    @Test
    fun shouldCloseActiveElectionAndElectNewMember() {
        val savedEntity = ElectionTestData.createElectionEntity()
        val finishedEntity = savedEntity.copy(validTo = LocalDate.now())
        val newEntity =
            ElectionEntity(
                id = null,
                role = RoleTestData.createRoleId().toRoleEntity(),
                member = MemberTestData.createMemberId().toMemberEntity(),
                validFrom = LocalDate.now(),
                validTo = null,
            )

        `when`(electionRepository.findLatestEntryByRoleId(RoleTestData.createRoleId().id)).thenReturn(Optional.of(savedEntity))

        electionService.elect(RoleTestData.createRoleId(), MemberTestData.createMemberId())

        verify(electionRepository).save(finishedEntity)
        verify(electionRepository).save(newEntity)
    }

    @Test
    fun shouldFinishElection() {
        val savedEntity = ElectionTestData.createElectionEntity()
        val finishedEntity = savedEntity.copy(validTo = LocalDate.now())

        `when`(electionRepository.findLatestEntryByRoleId(RoleTestData.createRoleId().id)).thenReturn(Optional.of(savedEntity))

        electionService.finish(RoleTestData.createRoleId())

        verify(electionRepository).save(finishedEntity)
    }

    @Test
    fun shouldDoNothingIfThereIsNoActiveElection() {
        `when`(electionRepository.findLatestEntryByRoleId(RoleTestData.createRoleId().id)).thenReturn(Optional.empty())

        electionService.finish(RoleTestData.createRoleId())

        verify(electionRepository, never()).save(any())
    }
}
