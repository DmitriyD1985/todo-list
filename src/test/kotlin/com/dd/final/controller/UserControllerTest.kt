package com.dd.final.controller

import com.dd.final.dto.AddUserRequest
import com.dd.final.dto.AuthRequest
import com.dd.final.dto.LoginResponse
import com.dd.final.repository.UserRepository
import com.dd.final.security.error.BadRequestException
import com.dd.final.service.CustomUserService
import com.dd.final.util.JsonParser
import com.dd.final.util.JsonParser.toJson
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
class UserControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var customUserService: CustomUserService

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    val request = AuthRequest("user", "password")

    @Test
    @WithMockUser(username = "user", password = "password", authorities = [])
    fun login() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", authorities = [])
    fun loginNegative() {
        every { customUserService.login(request) } throws BadRequestException("access denied")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, request))
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn().response
    }

    @Test
    @WithMockUser(username = "user", password = "password", authorities = [])
    fun register() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.toJson(objectMapper, request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response
    }
}