package com.example.todo.list.controller

import com.example.todo.list.model.RecordEntity
import com.example.todo.list.service.RecordService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/admin")
class AdminRecordController(
    private val recordService: RecordService
) {
    @PutMapping("/update/{id}")
    fun update(@RequestBody recordEntity: RecordEntity, @PathVariable id: Long) =
        recordService.update(recordEntity, id)

    @DeleteMapping("/delete/{id}")
    fun delete(@PathVariable id: Long) = recordService.delete(id)
}