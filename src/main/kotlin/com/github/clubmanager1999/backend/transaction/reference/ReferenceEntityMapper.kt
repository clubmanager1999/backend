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

import com.github.clubmanager1999.backend.creditor.CreditorEntityMapper
import com.github.clubmanager1999.backend.donor.DonorEntityMapper
import com.github.clubmanager1999.backend.member.MemberEntityMapper
import org.springframework.stereotype.Service

@Service
class ReferenceEntityMapper(
    val creditorEntityMapper: CreditorEntityMapper,
    val donorEntityMapper: DonorEntityMapper,
    val memberEntityMapper: MemberEntityMapper,
) {
    fun toExistingReference(referenceEntity: ReferenceEntity): ExistingReference {
        return when (referenceEntity) {
            is CreditorReferenceEntity -> {
                ExistingCreditorReference(creditor = creditorEntityMapper.toExistingCreditor(referenceEntity.creditor))
            }
            is DonorReferenceEntity -> {
                ExistingDonorReference(donor = donorEntityMapper.toExistingDonor(referenceEntity.donor))
            }

            is MemberReferenceEntity -> {
                ExistingMemberReference(member = memberEntityMapper.toExistingMember(emptyList(), referenceEntity.member))
            }

            else -> {
                throw RuntimeException("Mapping for ${referenceEntity.javaClass.simpleName} is not implemented")
            }
        }
    }

    fun toReferenceEntity(newReference: NewReference): ReferenceEntity {
        return when (newReference) {
            is NewCreditorReference -> {
                CreditorReferenceEntity(id = null, creditor = creditorEntityMapper.toCreditorEntity(newReference.creditor))
            }
            is NewDonorReference -> {
                DonorReferenceEntity(id = null, donor = donorEntityMapper.toDonorEntity(newReference.donor))
            }
            is NewMemberReference -> {
                MemberReferenceEntity(id = null, member = memberEntityMapper.toMemberEntity(newReference.member))
            }
        }
    }
}
