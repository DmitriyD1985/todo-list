package com.example.todo.list.dto

data class JwtResponse(
    private val token: String,
    private val userName: String,
    private val roles: List<String>,
    private val type: String = "Bearer",
)