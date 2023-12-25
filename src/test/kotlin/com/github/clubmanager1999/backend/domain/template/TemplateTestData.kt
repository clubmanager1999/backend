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
package com.github.clubmanager1999.backend.domain.template

object TemplateTestData {
    const val ID = 51L

    const val NAME = "business report"

    const val TEMPLATE = "{{data}}"

    fun createNewTemplate(): NewTemplate {
        return NewTemplate(
            name = NAME,
            template = TEMPLATE,
        )
    }

    fun createEmptyNewTemplate(): NewTemplate {
        return NewTemplate(
            name = "",
            template = TEMPLATE,
        )
    }

    fun createExistingTemplate(): ExistingTemplate {
        return ExistingTemplate(
            id = ID,
            name = NAME,
            template = TEMPLATE,
        )
    }

    fun createTemplateEntity(): TemplateEntity {
        return TemplateEntity(
            id = ID,
            name = NAME,
            template = TEMPLATE,
        )
    }

    fun createTemplateId(): TemplateId {
        return TemplateId(ID)
    }
}
