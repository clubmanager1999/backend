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
package com.github.clubmanager1999.backend.role

import com.github.clubmanager1999.backend.role.RoleTestData.ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoleMappingExtensionsTest {
    @Test
    fun shouldMapRoleEntityToExistingRole() {
        assertThat(
            RoleTestData.createRoleEntity().toExistingRole(),
        )
            .isEqualTo(RoleTestData.createExistingRole())
    }

    @Test
    fun shouldMapRoleEntityToExistingRoleWithMissingFields() {
        assertThat(
            RoleTestData.createRoleEntity().toExistingRole().copy(holder = null),
        )
            .isEqualTo(RoleTestData.createExistingRole().copy(holder = null))
    }

    @Test
    fun shouldMapNewRoleToRoleEntityWithId() {
        assertThat(
            RoleTestData.createNewRole().toRoleEntity(ID),
        )
            .isEqualTo(RoleTestData.createRoleEntity().copy(holder = null))
    }

    @Test
    fun shouldMapRoleIdToRoleEntity() {
        assertThat(
            RoleTestData.createRoleId().toRoleEntity(),
        )
            .isEqualTo(RoleTestData.createRoleEntity().copy(name = "", permissions = emptySet(), holder = null))
    }
}
