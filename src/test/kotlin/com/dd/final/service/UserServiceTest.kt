package com.dd.final.service

import com.dd.final.dto.AddUserRequest
import com.dd.final.dto.LoginResponse
import com.dd.final.model.Role
import com.dd.final.model.User
import com.dd.final.repository.UserRepository
import com.dd.final.security.UserDetailImpl
import com.dd.final.security.error.BadRequestException
import com.dd.final.security.jwt.JwtUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.security.Principal

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<BCryptPasswordEncoder>()
    private val authenticationManager = mockk<AuthenticationManager>()
    private val jwtUtils = mockk<JwtUtils>()

    private val customUserService = CustomUserService(userRepository, passwordEncoder, authenticationManager, jwtUtils)

    @Test
    fun loadUserByUsername() {
        val user = User(username = "user", password = "password", roles = hashSetOf(Role.ROLE_USER))

        every { userRepository.findByUsername(user.username) } returns user

        val result = customUserService.loadUserByUsername(user.username)

        assert(result is UserDetailImpl)
    }
    @Test
    fun login() {
        val addUserRequest = AddUserRequest(username = "user", password = "password", roles = hashSetOf("ROLE_USER"))
        val user = User(username = "user", password = "1111", roles = hashSetOf(Role.ROLE_USER))
        val authRequest = addUserRequest.toAuthRequest()
        val userDetails = UserDetailImpl(user) as UserDetails
        val usernameAuthentication = UsernamePasswordAuthenticationToken(
            authRequest.username,
            authRequest.password
        )
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

        every { userRepository.existsByUsername(addUserRequest.username) } returns false
        every { passwordEncoder.encode(addUserRequest.password) } returns "1111"
        every { userRepository.save(any()) } returns user
        every { userRepository.findByUsername(user.username) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true
        every { authenticationManager.authenticate(usernameAuthentication) } returns authentication
        every { jwtUtils.generateJwtToken(authentication) } returns "123"

        val result = customUserService.login(authRequest)
        assert(!(result.body as LoginResponse).isAdmin)
        assert((result.body as LoginResponse).token == "123")
    }

    @Test
    fun register() {
        val addUserRequest = AddUserRequest(username = "user", password = "password", roles = hashSetOf("ROLE_USER"))

        every { userRepository.existsByUsername(addUserRequest.username) } returns true

        assertThrows<BadRequestException> {
            customUserService.register(addUserRequest)
        }
    }

    @Test
    fun register2() {
        val addUserRequest = AddUserRequest(username = "user", password = "password", roles = hashSetOf("ROLE_USER"))
        val user = User(username = "user", password = "1111", roles = hashSetOf(Role.ROLE_USER))
        val authRequest = addUserRequest.toAuthRequest()
        val userDetails = UserDetailImpl(user) as UserDetails
        val usernameAuthentication = UsernamePasswordAuthenticationToken(
            authRequest.username,
            authRequest.password
        )
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

        every { userRepository.existsByUsername(addUserRequest.username) } returns false
        every { passwordEncoder.encode(addUserRequest.password) } returns "1111"
        every { userRepository.save(any()) } returns user
        every { userRepository.findByUsername(user.username) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true
        every { authenticationManager.authenticate(usernameAuthentication) } returns authentication
        every { jwtUtils.generateJwtToken(authentication) } returns "123"

        val result = customUserService.register(addUserRequest)
        assert(!(result.body as LoginResponse).isAdmin)
        assert((result.body as LoginResponse).token == "123")
    }
}