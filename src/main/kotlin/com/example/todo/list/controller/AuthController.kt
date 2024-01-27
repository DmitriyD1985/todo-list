package com.example.todo.list.controller

import com.example.todo.list.dto.JwtResponse
import com.example.todo.list.dto.LoginDto
import com.example.todo.list.dto.SignupRequest
import com.example.todo.list.model.ApplicationUser
import com.example.todo.list.model.ApplicationUserRole.Companion.parseKnown
import com.example.todo.list.model.UserRole
import com.example.todo.list.repository.RoleRepository
import com.example.todo.list.repository.UserRepository
import com.example.todo.list.security.JwtUtils
import com.example.todo.list.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val customUserDetailsService: CustomUserDetailsService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils
) {
    @PostMapping("/signin")
    fun authUser(@RequestBody loginRequest: LoginDto): ResponseEntity<*> {

        val authentication = authenticationManager
            .authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.name,
                    loginRequest.password
                )
            )
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.generateJwtToken(authentication)
        val userDetails = authentication.principal as ApplicationUser
        val roles = customUserDetailsService.loadUserByUsername(userDetails.username).authorities.toList().map { it.authority }

        return ResponseEntity.ok<Any>(
            JwtResponse(
                token = jwt,
                userDetails.username,
                roles
            )
        )
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signupRequest: SignupRequest): String {
        println("----------------------$signupRequest")
        if (userRepository.existsByUsername(signupRequest.name)) {
            return "Error: Username is exist"
        }

        val reqRoles = parseKnown(signupRequest.roles).map { UserRole(name = it) }.toSet()

        val user = ApplicationUser(
            name = signupRequest.name,
            password = passwordEncoder.encode(signupRequest.password)
        )

        user.roles = reqRoles
        userRepository.save(user)
        return "User CREATED"
    }
}