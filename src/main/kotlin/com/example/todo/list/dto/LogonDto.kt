package com.example.todo.list.dto

import com.example.todo.list.model.ApplicationUser

data class LogonDto(
    val userName: String,
    val password: String,
    var role: String
) {
    fun toApplicationUser() = ApplicationUser(
        username = userName,
        password = password,
        roles = emptySet()
    )
}