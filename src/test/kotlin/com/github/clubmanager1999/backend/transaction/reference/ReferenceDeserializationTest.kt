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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.clubmanager1999.backend.creditor.CreditorTestData
import com.github.clubmanager1999.backend.donor.DonorTestData
import com.github.clubmanager1999.backend.member.MemberTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class ReferenceDeserializationTest {
    val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    private val existingReferences =
        listOf(
            ExistingCreditorReference(creditor = CreditorTestData.createExistingCreditor()),
            ExistingDonorReference(donor = DonorTestData.createExistingDonor()),
            ExistingMemberReference(member = MemberTestData.createExistingMember()),
        )

    private val newReferences =
        listOf(
            NewCreditorReference(creditor = CreditorTestData.createCreditorId()),
            NewDonorReference(donor = DonorTestData.createDonorId()),
            NewMemberReference(member = MemberTestData.createMemberId()),
        )

    @TestFactory
    fun shouldDeserializeExistingReferences(): List<DynamicTest> {
        return existingReferences
            .map { reference ->
                DynamicTest.dynamicTest(reference.javaClass.simpleName) {
                    val json = this.javaClass.getResource("${reference.javaClass.simpleName}.json")?.readText()
                    val deserialized = objectMapper.readValue(json, ExistingReference::class.java)
                    assertThat(deserialized).isEqualTo(reference)
                }
            }
    }

    @TestFactory
    fun shouldDeserializeNewReferences(): List<DynamicTest> {
        return newReferences
            .map { reference ->
                DynamicTest.dynamicTest(reference.javaClass.simpleName) {
                    val json = this.javaClass.getResource("${reference.javaClass.simpleName}.json")?.readText()
                    val deserialized = objectMapper.readValue(json, NewReference::class.java)
                    assertThat(deserialized).isEqualTo(reference)
                }
            }
    }
}
