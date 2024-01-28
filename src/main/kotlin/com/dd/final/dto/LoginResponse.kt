package com.dd.final.dto

data class LoginResponse(
    var token: String,
    val id: Long,
    val username: String,
    val isAdmin: Boolean = false
)