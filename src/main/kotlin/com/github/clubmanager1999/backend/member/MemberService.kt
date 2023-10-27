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

import org.springframework.stereotype.Service

@Service
class MemberService(
    val memberEntityMapper: MemberEntityMapper,
    val memberRepository: MemberRepository,
) {
    fun get(id: Long): ExistingMember {
        return memberRepository
            .findById(id)
            .map { memberEntityMapper.toExistingMember(it) }
            .orElseThrow { MemberNotFoundException(id) }
    }

    fun getAll(): List<ExistingMember> {
        return memberRepository.findAll().map { memberEntityMapper.toExistingMember(it) }
    }

    fun create(newMember: NewMember): ExistingMember {
        return newMember
            .let { memberEntityMapper.toMemberEntity(null, it) }
            .let { memberRepository.save(it) }
            .let { memberEntityMapper.toExistingMember(it) }
    }

    fun update(
        id: Long,
        newMember: NewMember,
    ): ExistingMember {
        return memberRepository
            .findById(id)
            .orElseThrow { MemberNotFoundException(id) }
            .let {
                memberEntityMapper.toMemberEntity(id, newMember)
            }
            .let { memberRepository.save(it) }
            .let { memberEntityMapper.toExistingMember(it) }
    }

    fun delete(id: Long) {
        memberRepository.deleteById(id)
    }
}
