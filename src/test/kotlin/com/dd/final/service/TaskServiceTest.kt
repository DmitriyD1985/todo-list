package com.dd.final.service

import com.dd.final.dto.AddTaskRequest
import com.dd.final.dto.UpdateTaskRequest
import com.dd.final.model.Role
import com.dd.final.model.Task
import com.dd.final.model.User
import com.dd.final.repository.TaskRepository
import com.dd.final.repository.UserRepository
import com.dd.final.security.UserDetailImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails


class TaskServiceTest {

    private val taskRepository = mockk<TaskRepository>()
    private val userRepository = mockk<UserRepository>()

    private val taskService = TaskService(taskRepository, userRepository)

    @Test
    fun addTask() {
        val addTaskRequest = AddTaskRequest(1, "mnogo del")
        val user = User(username = "user", password = "password", roles = hashSetOf(Role.ROLE_USER))
        val userDetails = UserDetailImpl(user) as UserDetails
        val newTask = Task(description = addTaskRequest.description)

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        every { taskRepository.save(any()) } returns newTask
        every { userRepository.findByUsername(userDetails.username) } returns user
        every { userRepository.save(user)} returns user

        val result = taskService.addTask(addTaskRequest)
        assert((result.body as Task).description == "mnogo del")
    }

    @Test
    fun getTasks() {
        val user = User(username = "user", password = "password", roles = hashSetOf(Role.ROLE_USER))
        val userDetails = UserDetailImpl(user) as UserDetails

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        every { userRepository.findByUsername(userDetails.username) } returns user
        every { userRepository.save(user)} returns user

        val result = taskService.getTasks().body as List<*>

        assert(user.tasks.size == result.size)
    }

    @Test
    fun updateTask() {
        val user = User(username = "user", password = "password", roles = hashSetOf(Role.ROLE_ADMIN))
        val userDetails = UserDetailImpl(user) as UserDetails
        val updateTask = UpdateTaskRequest(1, description = "some new description", true)
        val task = updateTask.toDto()

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        every { userRepository.findByUsername(userDetails.username) } returns user
        every { taskRepository.save(any())} returns task

        val result = taskService.updateTask(updateTask)

        assert(result.body?.description == task.description)
    }

    @Test
    fun deleteTask() {
        val user = User(username = "user", password = "password", roles = hashSetOf(Role.ROLE_ADMIN))
        val userDetails = UserDetailImpl(user) as UserDetails
        val taskId = 1L

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        every { userRepository.findByUsername(userDetails.username) } returns user
        every { taskRepository.deleteById(taskId)} returns Unit

        val result = taskService.deleteTask(taskId)

        verify(exactly = 1) { taskRepository.deleteById(taskId) }
    }
}