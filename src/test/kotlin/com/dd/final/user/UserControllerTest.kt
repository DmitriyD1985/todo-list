package com.dd.final.user

import com.dd.final.dto.AddTaskRequest
import com.dd.final.dto.AddUserRequest
import com.dd.final.dto.AuthRequest
import com.dd.final.dto.LoginResponse
import com.dd.final.model.Role
import com.dd.final.model.Task
import com.dd.final.model.User
import com.dd.final.repository.UserRepository
import com.dd.final.service.TaskService
import com.dd.final.service.UserService
import com.dd.final.util.JsonParser.fromJson
import com.dd.final.util.JsonParser.toJson
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var taskService: TaskService

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    val request = AddUserRequest("user", "Test12345!", setOf("ROLE_USER"))

    @Test
    fun givenValidCredentials_whenRegister_thenSuccess() {
        mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(objectMapper, request)))
            .andExpect(status().isOk)
            .andReturn().response
        assertThat(userRepository.findByUsername("Test_user")).isNotNull
    }

    @Test
    fun givenTakenUsername_whenRegister_thenBadRequest() {
        userService.register(request)
        val response = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(objectMapper, request)))
            .andExpect(status().isBadRequest)
            .andReturn().response
        assertThat(response.contentAsString).contains("Username is already taken")
    }

    @Test
    fun givenUnacceptedCredentials_whenRegister_thenBadRequest() {
        val unacceptedRequest = AuthRequest("tt", "dsds")
        val response = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(objectMapper, unacceptedRequest)))
            .andExpect(status().isBadRequest)
            .andReturn().response
        assertThat(response.contentAsString).contains("password size must be between 8 and 40")
        assertThat(response.contentAsString).contains("username size must be between 3 and 20")
    }

    @Test
    fun givenValidCredentials_whenLogin_thenSuccess() {
        userService.register(request)
        val response = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(objectMapper, request)))
            .andExpect(status().isOk)
            .andReturn().response
        val loginResponse = fromJson(objectMapper, response.contentAsString, LoginResponse::class.java)
        assertThat(loginResponse.id).isNotNull
        assertThat(loginResponse.username).isNotNull
        assertThat(loginResponse.token).isNotNull
        assertThat(loginResponse.isAdmin).isTrue
    }

    @Test
    fun givenNonExistedUser_whenLogin_thenBadRequest() {
        val response = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(objectMapper, request)))
            .andExpect(status().isBadRequest)
            .andReturn().response
        assertThat(response.contentAsString).contains("Not found")
    }

    @Test
    fun givenWrongCredentials_whenLogin_thenUnAuthorized() {
        val invalidERequest = AuthRequest("Test_user", "InvalidPassword")
        userService.register(request)
        val response = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(objectMapper, invalidERequest)))
            .andExpect(status().isUnauthorized)
            .andReturn().response
        assertThat(response.contentAsString).contains("Invalid credentials")
    }

    @Test
    fun givenDisabledUser_whenLogin_thenForbidden() {
        val disabledRequest = AuthRequest("DisabledUser", "InvalidPassword")
        userRepository.save(
            User(
                username = disabledRequest.username,
                password = passwordEncoder.encode(disabledRequest.password),
                roles = hashSetOf(Role.ROLE_USER, Role.ROLE_ADMIN),
                enabled = false
            )
        )
        val response = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(objectMapper, disabledRequest)))
            .andExpect(status().isForbidden)
            .andReturn().response
        assertThat(response.contentAsString).contains("The user is not enabled")
    }

    @Test
    fun givenAdminRequest_whenAddUser_thenSuccess() {
        val adminUser = addAdmin()

        val addUserRequest = AddUserRequest("testUser", "password", setOf("ROLE_ADMIN"))

        val response = mockMvc.perform(post("/add_user")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, adminUser.token)
            .content(toJson(objectMapper, addUserRequest)))
            .andExpect(status().isOk)
            .andReturn().response

        assertThat(userRepository.findByUsername(addUserRequest.username)?.roles == hashSetOf(Role.ROLE_USER))
    }

     @Test
    fun givenTakenUser_whenAddUser_thenBadRequest() {
        val adminUser = addAdmin()

        val addUserRequest = AddUserRequest("testUser", "password", setOf("ROLE_USER"))
        userService.register(addUserRequest)
        val response = mockMvc.perform(post("/add_user")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, adminUser.token)
            .content(toJson(objectMapper, addUserRequest)))
            .andExpect(status().isBadRequest)
            .andReturn().response

        assertThat(response.contentAsString).contains("Username is already taken")
    }

    @Test
    fun givenAdminRequest_whenSuspendUser_thenSuccess() {
        val adminUser = addAdmin()
        val task = addTask(adminUser.id)
        task.id?.let { addUser(it) }
        val addedUserId = userRepository.findByUsername("testUser")?.id
        val response = mockMvc.perform(put("/suspend_user/{id}", addedUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, adminUser.token))
            .andExpect(status().isOk)
            .andReturn().response

        assertThat(!userRepository.findByUsername("testUser")?.enabled!!)
    }

    @Test
    fun givenUserNotFound_whenSuspendUser_thenBadRequest() {
        val adminUser = addAdmin()
        val response = mockMvc.perform(put("/suspend_user/{id}", 999)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, adminUser.token))
            .andExpect(status().isBadRequest)
            .andReturn().response

        assertThat(response.contentAsString).contains("Error: User not found.")
    }

    private fun addAdmin(): LoginResponse {
        val registerRequest = AddUserRequest("testAdmin", "password", setOf("ROLE_ADMIN"))
        return (userService.register(registerRequest).body as LoginResponse)
    }

    private fun addUser(taskId : Long = 1) {
        val addUserRequest = AddUserRequest("testUser", "password", setOf("ROLE_USER"))
        userService.register(addUserRequest)
    }

    private fun loginAddedUser(): LoginResponse {
        val authRequest = AuthRequest("ser", "password")
        return userService.login(authRequest).body as LoginResponse
    }

    private fun addTask(adminId: Long) = taskService.addTask(AddTaskRequest(1,"mnogo del")).body as Task
}


