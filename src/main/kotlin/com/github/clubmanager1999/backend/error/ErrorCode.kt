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
package com.github.clubmanager1999.backend.error

enum class ErrorCode {
    INTERNAL_ERROR,
    VALIDATION_ERROR,
    MEMBER_NOT_FOUND,
    SUBJECT_NOT_FOUND,
    MEMBERSHIP_NOT_FOUND,
    CLIENT_NOT_FOUND,
    ROLE_NOT_FOUND,
    TRANSACTION_NOT_FOUND,
    DONOR_NOT_FOUND,
    CREDITOR_NOT_FOUND,
    RECEIPT_NOT_FOUND,
    MAPPING_NOT_FOUND,
    PURPOSE_NOT_FOUND,
    AREA_NOT_FOUND,
    OVERLAPPING_RECEIPT,
    TEMPLATE_NOT_FOUND,
}
