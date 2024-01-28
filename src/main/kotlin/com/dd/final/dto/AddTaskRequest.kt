package com.dd.final.dto

data class AddTaskRequest(
    val id: Long = 0,
    val description: String,
    val done: Boolean = false,
)