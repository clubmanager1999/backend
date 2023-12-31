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

import com.github.clubmanager1999.backend.domain.member.toExistingMember
import com.github.clubmanager1999.backend.domain.member.toMemberEntity
import com.github.clubmanager1999.backend.domain.role.toExistingRole
import com.github.clubmanager1999.backend.domain.role.toRoleEntity

fun ElectionEntity.toExistingElection(): ExistingElection {
    return ExistingElection(
        id = this.id!!,
        role = this.role.toExistingRole(),
        member = this.member.toExistingMember(),
        validFrom = this.validFrom,
        validTo = this.validTo,
    )
}

fun NewElection.toElectionEntity(id: Long?): ElectionEntity {
    return ElectionEntity(
        id = id,
        role = this.role.toRoleEntity(),
        member = this.member.toMemberEntity(),
        validFrom = this.validFrom,
        validTo = this.validTo,
    )
}
