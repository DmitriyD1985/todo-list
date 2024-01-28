package com.dd.final.dto

import com.dd.final.model.Task

class UpdateTaskRequest(
    val id: Long,
    val description: String,
    val done: Boolean
) {
    fun toDto() = Task(
        id = this.id,
        description = this.description,
        done = this.done
    )
}