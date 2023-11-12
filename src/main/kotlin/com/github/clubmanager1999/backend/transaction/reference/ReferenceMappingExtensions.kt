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
package com.github.clubmanager1999.backend.transaction.reference

import com.github.clubmanager1999.backend.creditor.CreditorId
import com.github.clubmanager1999.backend.creditor.toCreditorEntity
import com.github.clubmanager1999.backend.creditor.toExistingCreditor
import com.github.clubmanager1999.backend.donor.DonorId
import com.github.clubmanager1999.backend.donor.toDonorEntity
import com.github.clubmanager1999.backend.donor.toExistingDonor
import com.github.clubmanager1999.backend.member.MemberId
import com.github.clubmanager1999.backend.member.toExistingMember
import com.github.clubmanager1999.backend.member.toMemberEntity

fun ReferenceEntity.toExistingReference(): ExistingReference {
    return when (this) {
        is CreditorReferenceEntity -> {
            ExistingCreditorReference(creditor = this.creditor.toExistingCreditor())
        }
        is DonorReferenceEntity -> {
            ExistingDonorReference(donor = this.donor.toExistingDonor())
        }

        is MemberReferenceEntity -> {
            ExistingMemberReference(member = this.member.toExistingMember())
        }

        else -> {
            throw RuntimeException("Mapping for ${this.javaClass.simpleName} is not implemented")
        }
    }
}

fun NewReference.toReferenceEntity(): ReferenceEntity {
    return when (this) {
        is NewCreditorReference -> {
            CreditorReferenceEntity(id = null, creditor = this.creditor.toCreditorEntity())
        }
        is NewDonorReference -> {
            DonorReferenceEntity(id = null, donor = this.donor.toDonorEntity())
        }
        is NewMemberReference -> {
            MemberReferenceEntity(id = null, member = this.member.toMemberEntity())
        }
    }
}

fun ExistingReference.toNewReference(): NewReference {
    return when (this) {
        is ExistingCreditorReference -> {
            NewCreditorReference(creditor = CreditorId(this.creditor.id))
        }
        is ExistingDonorReference -> {
            NewDonorReference(donor = DonorId(this.donor.id))
        }
        is ExistingMemberReference -> {
            NewMemberReference(member = MemberId(this.member.id))
        }
    }
}
