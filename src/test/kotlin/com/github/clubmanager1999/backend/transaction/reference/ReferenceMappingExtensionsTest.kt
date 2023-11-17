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

import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.creditor.toCreditorEntity
import com.github.clubmanager1999.backend.donor.DonorTestData
import com.github.clubmanager1999.backend.donor.toDonorEntity
import com.github.clubmanager1999.backend.member.MemberTestData
import com.github.clubmanager1999.backend.member.toMemberEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ReferenceMappingExtensionsTest {
    @Test
    fun shouldMapReferenceEntityToExistingReference() {
        assertThat(
            CreditorReferenceEntity(id = CreditorTestData.ID, creditor = CreditorTestData.createCreditorEntity()).toExistingReference(),
        )
            .isEqualTo(ExistingCreditorReference(creditor = CreditorTestData.createExistingCreditor()))

        assertThat(
            DonorReferenceEntity(
                id = DonorTestData.ID,
                donor = DonorTestData.createDonorEntity(),
            ).toExistingReference(),
        )
            .isEqualTo(ExistingDonorReference(donor = DonorTestData.createExistingDonor()))

        assertThat(
            MemberReferenceEntity(id = ReferenceTestData.ID, member = MemberTestData.createMemberEntity()).toExistingReference(),
        )
            .isEqualTo(ExistingMemberReference(member = MemberTestData.createExistingMember()))

        assertThatThrownBy { UnknownEntity().toExistingReference() }
            .isInstanceOf(RuntimeException::class.java)
            .extracting { it.message }
            .isEqualTo("Mapping for UnknownEntity is not implemented")
    }

    class UnknownEntity : ReferenceEntity()

    @Test
    fun shouldMapNewReferenceToReferenceEntity() {
        assertThat(NewCreditorReference(creditor = CreditorTestData.createCreditorId()).toReferenceEntity())
            .isEqualTo(CreditorReferenceEntity(id = null, creditor = CreditorTestData.createCreditorId().toCreditorEntity()))

        assertThat(NewDonorReference(donor = DonorTestData.createDonorId()).toReferenceEntity())
            .isEqualTo(DonorReferenceEntity(id = null, donor = DonorTestData.createDonorId().toDonorEntity()))

        assertThat(NewMemberReference(member = MemberTestData.createMemberId()).toReferenceEntity())
            .isEqualTo(MemberReferenceEntity(id = null, member = MemberTestData.createMemberId().toMemberEntity()))
    }

    @Test
    fun shouldMapExistingCreditorReferenceToNewReference() {
        assertThat(ExistingCreditorReference(creditor = CreditorTestData.createExistingCreditor()).toNewReference())
            .isEqualTo(NewCreditorReference(creditor = CreditorTestData.createCreditorId()))

        assertThat(ExistingDonorReference(donor = DonorTestData.createExistingDonor()).toNewReference())
            .isEqualTo(NewDonorReference(donor = DonorTestData.createDonorId()))

        assertThat(ExistingMemberReference(member = MemberTestData.createExistingMember()).toNewReference())
            .isEqualTo(NewMemberReference(member = MemberTestData.createMemberId()))
    }
}
