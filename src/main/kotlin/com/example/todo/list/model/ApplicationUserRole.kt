package com.example.todo.list.model

import org.springframework.security.core.GrantedAuthority

enum class ApplicationUserRole: GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_GOD;

    override fun getAuthority() = this.name

    companion object {
        fun parseKnown(roleCodes: Collection<String>) = roleCodes.map { enumValueOf<ApplicationUserRole>(it) }
    }
}