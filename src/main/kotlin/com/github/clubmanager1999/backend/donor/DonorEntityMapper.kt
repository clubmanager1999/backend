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
package com.github.clubmanager1999.backend.donor

import com.github.clubmanager1999.backend.member.Address
import org.springframework.stereotype.Service

@Service
class DonorEntityMapper {
    fun toExistingDonor(donorEntity: DonorEntity): ExistingDonor {
        return ExistingDonor(
            id = donorEntity.id!!,
            firstName = donorEntity.firstName,
            lastName = donorEntity.lastName,
            address = donorEntity.address,
        )
    }

    fun toDonorEntity(
        id: Long?,
        newDonor: NewDonor,
    ): DonorEntity {
        return DonorEntity(
            id = id,
            firstName = newDonor.firstName,
            lastName = newDonor.lastName,
            address = newDonor.address,
        )
    }

    fun toDonorEntity(donorId: DonorId): DonorEntity {
        return DonorEntity(
            id = donorId.id,
            firstName = "",
            lastName = "",
            address = Address("", "", "", ""),
        )
    }
}