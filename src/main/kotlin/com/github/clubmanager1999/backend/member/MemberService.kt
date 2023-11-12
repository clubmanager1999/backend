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
import com.github.clubmanager1999.backend.oidc.OidcUserMapper
import com.github.clubmanager1999.backend.oidc.Subject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MemberService(
    val memberRepository: MemberRepository,
    val oidcUserMapper: OidcUserMapper,
    val oidcAdminService: OidcAdminService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun get(id: Long): ExistingMemberWithRoles {
        return memberRepository
            .findById(id)
            .map { it.toExistingMember().withRoles(getRoles(it)) }
            .orElseThrow { MemberNotFoundException(id) }
    }

    fun get(subject: Subject): ExistingMemberWithRoles {
        return memberRepository
            .findBySubject(subject.id)
            .map { it.toExistingMember().withRoles(getRoles(it)) }
            .orElseThrow { SubjectNotFoundException(subject.id) }
    }

    fun getAll(): List<ExistingMemberWithRoles> {
        return memberRepository.findAll().map { it.toExistingMember().withRoles(getRoles(it)) }
    }

    fun create(newMember: NewMember): ExistingMemberWithRoles {
        val oidcUser = oidcUserMapper.toOidcUser(newMember)
        val subject = oidcAdminService.createUser(oidcUser)

        try {
            oidcAdminService.resetPassword(subject)
        } catch (e: Exception) {
            logger.error("Failed to reset password", e)
        }

        return newMember
            .toMemberEntity(null, subject.id)
            .let { memberRepository.save(it) }
            .let { it.toExistingMember().withRoles(getRoles(it)) }
    }

    fun update(
        id: Long,
        newMember: NewMember,
    ): ExistingMemberWithRoles {
        val oidcUser = oidcUserMapper.toOidcUser(newMember)

        return memberRepository
            .findById(id)
            .orElseThrow { MemberNotFoundException(id) }
            .let {
                oidcAdminService.updateUser(Subject(it.subject), oidcUser)
                it
            }
            .let {
                newMember.toMemberEntity(it.id, it.subject)
            }
            .let { memberRepository.save(it) }
            .let { it.toExistingMember().withRoles(getRoles(it)) }
    }

    fun delete(id: Long) {
        memberRepository
            .findById(id)
            .ifPresent { oidcAdminService.deleteUser(Subject(it.subject)) }

        memberRepository.deleteById(id)
    }

    fun getRoles(memberEntity: MemberEntity): List<String> {
        return oidcAdminService.getUserRoles(Subject(memberEntity.subject)).map { it.name }
    }

    fun addRoleToMember(
        id: Long,
        role: String,
    ) {
        memberRepository
            .findById(id)
            .orElseThrow { MemberNotFoundException(id) }
            .let { oidcAdminService.addRoleToUser(Subject(it.subject), role) }
    }

    fun removeRoleFromMember(
        id: Long,
        role: String,
    ) {
        memberRepository
            .findById(id)
            .orElseThrow { MemberNotFoundException(id) }
            .let { oidcAdminService.removeRoleFromUser(Subject(it.subject), role) }
    }
}
