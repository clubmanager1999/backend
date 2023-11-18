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
package com.github.clubmanager1999.backend.election

import com.github.clubmanager1999.backend.member.MemberId
import com.github.clubmanager1999.backend.member.toMemberEntity
import com.github.clubmanager1999.backend.role.RoleId
import com.github.clubmanager1999.backend.role.toRoleEntity
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ElectionService(
    val electionRepository: ElectionRepository,
) {
    fun getAll(): List<ExistingElection> {
        return electionRepository.findAll().map { it.toExistingElection() }
    }

    fun elect(
        role: RoleId,
        member: MemberId,
    ) {
        electionRepository
            .findLatestEntryByRoleId(role.id)
            .map { it.copy(validTo = LocalDate.now()) }
            .ifPresent { electionRepository.save(it) }

        val election =
            ElectionEntity(
                id = null,
                role = role.toRoleEntity(),
                member = member.toMemberEntity(),
                validFrom = LocalDate.now(),
                validTo = null,
            )

        electionRepository.save(election)
    }

    fun finish(role: RoleId) {
        electionRepository
            .findLatestEntryByRoleId(role.id)
            .map { it.copy(validTo = LocalDate.now()) }
            .ifPresent { electionRepository.save(it) }
    }
}
