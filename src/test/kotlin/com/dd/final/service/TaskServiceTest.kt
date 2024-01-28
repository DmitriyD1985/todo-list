package com.dd.final.service

import com.dd.final.dto.AddTaskRequest
import com.dd.final.model.Role
import com.dd.final.model.Task
import com.dd.final.model.User
import com.dd.final.repository.TaskRepository
import com.dd.final.repository.UserRepository
import com.dd.final.security.UserDetailImpl
import io.mockk.every
import io.mockk.mockk
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
    }

    @Test
    fun updateTask() {
    }

    @Test
    fun deleteTask() {

    }
}