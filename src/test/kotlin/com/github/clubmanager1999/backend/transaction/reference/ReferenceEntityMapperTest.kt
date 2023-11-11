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
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.donor.DonorEntityMapper
import com.github.clubmanager1999.backend.donor.DonorTestData
import com.github.clubmanager1999.backend.member.MemberEntityMapper
import com.github.clubmanager1999.backend.member.MemberTestData
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ReferenceEntityMapperTest {
    @Mock lateinit var creditorEntityMapper: CreditorEntityMapper

    @Mock lateinit var donorEntityMapper: DonorEntityMapper

    @Mock lateinit var memberEntityMapper: MemberEntityMapper

    @InjectMocks lateinit var referenceEntityMapper: ReferenceEntityMapper

    @Test
    fun shouldMapReferenceEntityToExistingReference() {
        `when`(
            memberEntityMapper.toExistingMember(emptyList(), MemberTestData.createMemberEntity()),
        ).thenReturn(MemberTestData.createExistingMember())

        `when`(
            donorEntityMapper.toExistingDonor(DonorTestData.createDonorEntity()),
        ).thenReturn(DonorTestData.createExistingDonor())

        `when`(
            creditorEntityMapper.toExistingCreditor(CreditorTestData.createCreditorEntity()),
        ).thenReturn(CreditorTestData.createExistingCreditor())

        assertThat(
            referenceEntityMapper.toExistingReference(
                CreditorReferenceEntity(id = CreditorTestData.ID, creditor = CreditorTestData.createCreditorEntity()),
            ),
        )
            .isEqualTo(ExistingCreditorReference(creditor = CreditorTestData.createExistingCreditor()))

        assertThat(
            referenceEntityMapper.toExistingReference(
                DonorReferenceEntity(id = com.github.clubmanager1999.backend.donor.ID, donor = DonorTestData.createDonorEntity()),
            ),
        )
            .isEqualTo(ExistingDonorReference(donor = DonorTestData.createExistingDonor()))

        assertThat(
            referenceEntityMapper.toExistingReference(
                MemberReferenceEntity(id = ReferenceTestData.ID, member = MemberTestData.createMemberEntity()),
            ),
        )
            .isEqualTo(ExistingMemberReference(member = MemberTestData.createExistingMember()))

        Assertions.assertThatThrownBy { referenceEntityMapper.toExistingReference(UnknownEntity()) }
            .isInstanceOf(RuntimeException::class.java)
            .extracting { it.message }
            .isEqualTo("Mapping for UnknownEntity is not implemented")
    }

    class UnknownEntity : ReferenceEntity()

    @Test
    fun shouldMapNewReferenceToReferenceEntity() {
        `when`(
            memberEntityMapper.toMemberEntity(MemberTestData.createMemberId()),
        ).thenReturn(MemberTestData.createMemberEntity())

        `when`(
            donorEntityMapper.toDonorEntity(DonorTestData.createDonorId()),
        ).thenReturn(DonorTestData.createDonorEntity())

        `when`(
            creditorEntityMapper.toCreditorEntity(CreditorTestData.createCreditorId()),
        ).thenReturn(CreditorTestData.createCreditorEntity())

        assertThat(referenceEntityMapper.toReferenceEntity(NewCreditorReference(creditor = CreditorTestData.createCreditorId())))
            .isEqualTo(CreditorReferenceEntity(id = null, creditor = CreditorTestData.createCreditorEntity()))

        assertThat(referenceEntityMapper.toReferenceEntity(NewDonorReference(donor = DonorTestData.createDonorId())))
            .isEqualTo(DonorReferenceEntity(id = null, donor = DonorTestData.createDonorEntity()))

        assertThat(referenceEntityMapper.toReferenceEntity(NewMemberReference(member = MemberTestData.createMemberId())))
            .isEqualTo(MemberReferenceEntity(id = null, member = MemberTestData.createMemberEntity()))
    }
}
