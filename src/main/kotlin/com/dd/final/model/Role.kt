package com.dd.final.model

import com.dd.final.security.error.NotAnyRoleExist


enum class Role {
    ROLE_USER, ROLE_ADMIN;

    companion object {
        fun parse(roles: Set<String>) = roles.map { role -> Role.values().firstOrNull {it.name == role} ?: throw NotAnyRoleExist("no roles from request found") }
    }
}