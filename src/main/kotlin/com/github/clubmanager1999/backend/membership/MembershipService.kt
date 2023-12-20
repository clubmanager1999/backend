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
    val membershipRepository: MembershipRepository,
) {
    fun get(id: Long): ExistingMembership {
        return membershipRepository
            .findById(id)
            .map { it.toExistingMembership() }
            .orElseThrow { MembershipNotFoundException(id) }
    }

    fun getAll(): List<ExistingMembership> {
        return membershipRepository.findAll().map { it.toExistingMembership() }
    }

    fun create(newMembership: NewMembership): MembershipId {
        return newMembership
            .toMembershipEntity(null)
            .let { membershipRepository.save(it) }
            .toMembershipId()
    }

    fun update(
        id: Long,
        newMembership: NewMembership,
    ): MembershipId {
        return membershipRepository
            .findById(id)
            .orElseThrow { MembershipNotFoundException(id) }
            .let { newMembership.toMembershipEntity(it.id) }
            .let { membershipRepository.save(it) }
            .toMembershipId()
    }

    fun delete(id: Long) {
        membershipRepository.deleteById(id)
    }
}
