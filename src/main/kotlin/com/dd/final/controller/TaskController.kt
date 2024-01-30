package com.dd.final.controller

import com.dd.final.dto.AddTaskRequest
import com.dd.final.dto.MessageResponse
import com.dd.final.dto.UpdateTaskRequest
import com.dd.final.security.error.BadRequestException
import com.dd.final.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/task")
class TaskController(private var taskService: TaskService) {

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    fun add(@Valid @RequestBody addTaskRequest: AddTaskRequest): ResponseEntity<*> = try {
        taskService.addTask(addTaskRequest)
    } catch (e: Exception) {
        ResponseEntity
            .badRequest()
            .body(MessageResponse(e.message))
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get")
    fun get(): ResponseEntity<*> = try {
        taskService.getTasks()
    } catch (e: BadRequestException) {
        ResponseEntity
            .badRequest()
            .body(MessageResponse(e.message))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    fun update(
        @Valid @RequestBody newTask: UpdateTaskRequest,
    ) = try {
        taskService.updateTask(newTask)
    } catch (e: Exception) {
        val status = when (e) {
            is BadCredentialsException -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.FORBIDDEN
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    fun delete(@PathVariable id: Long) = try {
        taskService.deleteTask(id)
    } catch (e: BadRequestException) {
        ResponseEntity
            .badRequest()
            .body(MessageResponse(e.message))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleException(e: AccessDeniedException) = ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("You shell not pass")

}