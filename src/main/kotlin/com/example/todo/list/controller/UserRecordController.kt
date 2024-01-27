package com.example.todo.list.controller

import com.example.todo.list.model.RecordEntity
import com.example.todo.list.service.RecordService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/user")
class UserRecordController(
    private val recordService: RecordService
) {
    @GetMapping("/read/{name}")
    fun read(@PathVariable name: String) = recordService.read(name)

    @PostMapping("/create")
    fun create(@RequestBody recordEntity: RecordEntity) = recordService.create(recordEntity)
}