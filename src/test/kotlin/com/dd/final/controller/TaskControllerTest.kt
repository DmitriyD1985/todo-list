package com.dd.final.controller

import com.dd.final.dto.AddTaskRequest
import com.dd.final.repository.TaskRepository
import com.dd.final.repository.UserRepository
import com.dd.final.service.CustomUserService
import com.dd.final.service.TaskService
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.security.Key
import java.util.*


@WebMvcTest(TaskController::class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @MockBean
    private lateinit var taskService: TaskService

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var taskRepository: TaskRepository

    @MockBean
    private lateinit var customUserDetailsService: CustomUserService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    private val addTaskRequest = AddTaskRequest(1, "mnogo del")

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity()).build()
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = ["USER"])
    fun add() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/task/add")
                .header("authorization", generateJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = ["USER"])
    fun get() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/task/get")
                .header("authorization", generateJwtToken())
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun update() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/task/update")
                .header("authorization", generateAdminJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = ["USER"])
    fun updateNegative() {
        val exception: Exception =
            Assertions.assertThrows(Exception::class.java) {
                mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/task/update")
                        .header("authorization", generateJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTaskRequest))
                ).andExpect(MockMvcResultMatchers.status().isOk)
            }
        Assertions.assertNotNull(exception)
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun delete() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/task/delete/1")
                .header("authorization", generateAdminJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addTaskRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = ["USER"])
    fun deleteNegative() {
        val exception: Exception =
            Assertions.assertThrows(Exception::class.java) {
                mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/task/delete/1")
                        .header("authorization", generateAdminJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTaskRequest))
                ).andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn().response
            }
        Assertions.assertNotNull(exception)
    }

    private fun generateAdminJwtToken(): String {
        val keyBytes =
            Decoders.BASE64.decode("MegaLargeSigningSecretKeyForDemoApplicationMegaLargeSigningSecretKeyForDemoApplication")
        val key: Key = Keys.hmacShaKeyFor(keyBytes)
        val token = Jwts.builder()
            .setSubject("user")
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + 86400000))
            .signWith(key).compact()
        return "Bearer $token"
    }

    private fun generateJwtToken(): String {
        val keyBytes =
            Decoders.BASE64.decode("MegaLargeSigningSecretKeyForDemoApplicationMegaLargeSigningSecretKeyForDemoApplication")
        val key: Key = Keys.hmacShaKeyFor(keyBytes)
        val token = Jwts.builder()
            .setSubject("user")
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + 86400000))
            .signWith(key).compact()
        return "Bearer $token"
    }
}