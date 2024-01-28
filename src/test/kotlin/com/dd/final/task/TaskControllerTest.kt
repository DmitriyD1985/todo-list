package com.dd.final.task

import com.dd.final.dto.*
import com.dd.final.repository.TaskRepository
import com.dd.final.security.error.ValidationErrorProcessor
import com.dd.final.security.jwt.AuthEntryPointJwt
import com.dd.final.security.jwt.JwtUtils
import com.dd.final.service.TaskService
import com.dd.final.service.UserService
import com.dd.final.util.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@AutoConfigureMockMvc
@WebMvcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = [TaskService::class, TaskRepository::class])
@ActiveProfiles("test")
class TaskControllerTest{

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var taskService: TaskService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var validationErrorProcessor: ValidationErrorProcessor

    @Autowired
    private lateinit var authEntryPointJwt: AuthEntryPointJwt

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    private val addTaskRequest = AddTaskRequest(1, "mnogo del")

    @Test
    fun `add task is Success with user role`() {
        addUser2()
        val admin = addUser()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/task/add")
                .header(HttpHeaders.AUTHORIZATION, admin.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
        Assertions.assertThat(taskRepository.findById(1)).isNotNull
    }

    @Test
    fun givenUnprivilegedUser_whenAddtask_thenFailure() {
        addUser()
        val unprivilegedUser = userService.login(AuthRequest("testUser", "password")).body as LoginResponse
        mockMvc.perform(
            MockMvcRequestBuilders.post("/task/add")
                .header(HttpHeaders.AUTHORIZATION, unprivilegedUser.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn().response
    }

    @Test
    fun giventaskExists_whenGettaskById_thenSuccess() {
        val adminUser = addAdmin()
        val task = addTask()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/task/get")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, adminUser.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    fun giventaskNotFound_whenGettaskById_thenFailure() {
        val adminUser = addAdmin()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/task/get" )
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, adminUser.token)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response
        Assertions.assertThat(response.contentAsString).contains("Error: User doesn't have task.")
    }

    @Test
    fun giventaskExists_whenUpdatetask_thenSuccess() {
        val adminUser = addAdmin()
        val task = addTask()
        val request = UpdateTaskRequest(1, "mnogo del", true)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/task/update")
                .content(JsonParser.toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, adminUser.token)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
        Assertions.assertThat(response.contentAsString).contains("Grinta")
    }

    @Test
    fun giventaskNotFound_whenUpdatetask_thenFailure() {
        val adminUser = addAdmin()
        val request = UpdateTaskRequest(1, "mnogo del", true)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/task/update")
                .content(JsonParser.toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, adminUser.token)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response
        Assertions.assertThat(response.contentAsString).contains("Error: User doesn't have task.")
    }

    @Test
    fun giventaskExists_whenDeletetask_thenSuccess() {
        val adminUser = addAdmin()
        val task = addTask()
        val request = UpdateTaskRequest(1, "mnogo del", true)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/task/delete")
                .content(JsonParser.toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, adminUser.token)
        ).andExpect(MockMvcResultMatchers.status().isAccepted)
            .andReturn().response
    }

    @Test
    fun giventaskNotFound_whenDeletetask_thenFailure() {
        val adminUser = addAdmin()
        val request = UpdateTaskRequest(1, "mnogo del", true)
        val response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/task/delete")
                .content(JsonParser.toJson(objectMapper, request))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, adminUser.token)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response
        Assertions.assertThat(response.contentAsString).contains("Error: User doesn't have task.")
    }

    private fun addUser() = userService.register(
        AddUserRequest("user", "password", setOf("ROLE_USER"))
    ).body as LoginResponse

    private fun addUser2() = userService.register(
        AddUserRequest("user", "password", setOf("ROLE_USER"))
    )

    private fun addAdmin() = userService.register(AddUserRequest("testUser", "password", setOf("ROLE_ADMIN"))).body as LoginResponse

    private fun addTask() = taskService.addTask(addTaskRequest)
}