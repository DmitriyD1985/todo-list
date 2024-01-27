package com.example.todo.list.dto

data class SignupRequest(
    var name: String,
    val roles: Set<String>,
    val password: String,
)