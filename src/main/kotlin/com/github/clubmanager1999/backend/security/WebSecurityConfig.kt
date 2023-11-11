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

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val keycloakJwtConverter: KeycloakJwtConverter) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers("/api/members/*/roles/**").hasRole(Permission.MANAGE_ROLES.toString())

            it.requestMatchers("/api/members/**").hasRole(Permission.MANAGE_MEMBERS.toString())

            it.requestMatchers("/api/memberships/**").hasRole(Permission.MANAGE_MEMBERSHIPS.toString())

            it.requestMatchers("/api/roles/**").hasRole(Permission.MANAGE_ROLES.toString())

            it.requestMatchers("/api/transactions/**").hasRole(Permission.MANAGE_TRANSACTIONS.toString())

            it.requestMatchers("/api/donors/**").hasRole(Permission.MANAGE_DONORS.toString())

            it.requestMatchers("/api/creditors/**").hasRole(Permission.MANAGE_CREDITORS.toString())

            it.requestMatchers("/api/receipts/**").hasRole(Permission.MANAGE_RECEIPTS.toString())

            it.requestMatchers("/api/mappings/**").hasRole(Permission.MANAGE_MAPPINGS.toString())

            it.anyRequest().authenticated()
        }

        http.oauth2ResourceServer { oAuth2ResourceServerConfigurer ->
            oAuth2ResourceServerConfigurer.jwt { it.jwtAuthenticationConverter(keycloakJwtConverter) }
        }

        http.csrf { it.disable() }

        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        return http.build()
    }
}
