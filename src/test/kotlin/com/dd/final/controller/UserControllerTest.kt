package com.dd.final.controller

import com.dd.final.dto.AddUserRequest
import com.dd.final.dto.AuthRequest
import com.dd.final.repository.TaskRepository
import com.dd.final.repository.UserRepository
import com.dd.final.security.error.ValidationErrorProcessor
import com.dd.final.service.CustomUserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(UserController::class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var customUserService: CustomUserService

    @MockBean
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var taskRepository: TaskRepository

    @MockBean
    lateinit var validationErrorProcessor: ValidationErrorProcessor


    @Test
    fun login() {
        val authRequest = objectMapper.writeValueAsString(AuthRequest("user", "password"))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequest)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    fun register() {
        val addUserRequest = objectMapper.writeValueAsString(AddUserRequest("user", "password", setOf("ROLE_USER")))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addUserRequest)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

}