package com.dd.final.service

import com.dd.final.dto.AddUserRequest
import com.dd.final.model.Role
import com.dd.final.model.User
import com.dd.final.security.error.BadRequestException
import com.dd.final.security.jwt.JwtUtils
import com.dd.final.dto.AuthRequest
import com.dd.final.dto.LoginResponse
import com.dd.final.repository.UserRepository
import com.dd.final.security.UserDetailImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.stream.Collectors


@Service
class UserService: UserDetailsService {
    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder
    @Autowired
    lateinit var authenticationManager: AuthenticationManager
    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("Not found: $username")
        return UserDetailImpl(user)
    }

    fun login(authRequest: AuthRequest): ResponseEntity<Any> {
        val userDetail = loadUserByUsername(authRequest.username)

        if (!passwordEncoder.matches(authRequest.password, userDetail.password)) {
            throw BadCredentialsException("Invalid credentials")
        }

        if (!userDetail.isEnabled) {
            throw BadRequestException("The user is not enabled")
        }


        val authentication =
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authRequest.username,
                    authRequest.password
                )
            )
        SecurityContextHolder.getContext().authentication = authentication

        val jwtToken = jwtUtils.generateJwtToken(authentication)

        val userDetails = authentication.principal as UserDetailImpl
        val user = userDetails.user
        val roles = userDetails.authorities.stream()
            .map { item -> item.authority }
            .collect(Collectors.toList())


        user.token = jwtToken
        userRepository.save(user)

        return ResponseEntity.ok().body(
            LoginResponse(
                jwtToken,
                userDetails.getId() ?: 0,
                userDetails.username,
                user.isAdmin()
            )
        )
    }

    fun register(addUserRequest: AddUserRequest): ResponseEntity<Any> {
        if (userRepository.existsByUsername(addUserRequest.username)) {
            throw BadRequestException("Username already exist")
        }
        User(
            username = addUserRequest.username,
            password = passwordEncoder.encode(addUserRequest.password),
            roles = Role.parse(addUserRequest.roles).toHashSet()
        ).apply { userRepository.save(this) }
        return login(addUserRequest.toAuthRequest())
    }
}