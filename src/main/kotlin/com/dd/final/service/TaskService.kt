package com.dd.final.service

import com.dd.final.dto.AddTaskRequest
import com.dd.final.repository.UserRepository
import com.dd.final.dto.UpdateTaskRequest
import com.dd.final.model.Task
import com.dd.final.repository.TaskRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) {

    fun addTask(addTaskRequest: AddTaskRequest): ResponseEntity<*> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val user = userRepository.findByUsername(userDetails.username) ?: throw  UsernameNotFoundException("user nt found")
        val newTask = Task(
            description = addTaskRequest.description,
        ).apply { taskRepository.save(this) }
        val newUser = user.also{
            it.tasks.add(newTask)
        }
        userRepository.save(newUser)
        return ResponseEntity.ok().body(newTask)
    }

    fun getTasks(): ResponseEntity<*> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val tasks = userRepository.findByUsername(user.username)?.tasks
        return ResponseEntity.ok().body(tasks)
    }

    fun updateTask(updatedTask: UpdateTaskRequest) = ResponseEntity.ok().body(taskRepository.save(updatedTask.toDto()))


    fun deleteTask(id: Long) = ResponseEntity.ok().body(taskRepository.deleteById(id))
}
