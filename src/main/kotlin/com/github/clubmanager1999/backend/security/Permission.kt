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
package com.github.clubmanager1999.backend.security

enum class Permission {
    MANAGE_MEMBERS,
    MANAGE_MEMBERSHIPS,
    MANAGE_ROLES,
    MANAGE_TRANSACTIONS,
    MANAGE_DONORS,
    MANAGE_CREDITORS,
    MANAGE_RECEIPTS,
    MANAGE_MAPPINGS,
    MANAGE_PURPOSES,
    MANAGE_AREAS,
    MANAGE_TEMPLATES,
    ;

    fun getRoleName(): String {
        return this.name
            .lowercase()
            .replace("_", "-")
    }

    companion object {
        val byRoleName = Permission.entries.associateBy { it.getRoleName() }
    }
}
