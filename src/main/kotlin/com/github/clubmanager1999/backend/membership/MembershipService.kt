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

import org.springframework.stereotype.Service

@Service
class MembershipService(
    val membershipEntityMapper: MembershipEntityMapper,
    val membershipRepository: MembershipRepository,
) {
    fun get(id: Long): ExistingMembership {
        return membershipRepository
            .findById(id)
            .map { membershipEntityMapper.toExistingMembership(it) }
            .orElseThrow { MembershipNotFoundException(id) }
    }

    fun getAll(): List<ExistingMembership> {
        return membershipRepository.findAll().map { membershipEntityMapper.toExistingMembership(it) }
    }

    fun create(newMembership: NewMembership): ExistingMembership {
        return newMembership
            .let { membershipEntityMapper.toMembershipEntity(null, it) }
            .let { membershipRepository.save(it) }
            .let { membershipEntityMapper.toExistingMembership(it) }
    }

    fun update(
        id: Long,
        newMembership: NewMembership,
    ): ExistingMembership {
        return membershipRepository
            .findById(id)
            .orElseThrow { MembershipNotFoundException(id) }
            .let { membershipEntityMapper.toMembershipEntity(id, newMembership) }
            .let { membershipRepository.save(it) }
            .let { membershipEntityMapper.toExistingMembership(it) }
    }

    fun delete(id: Long) {
        membershipRepository.deleteById(id)
    }
}
