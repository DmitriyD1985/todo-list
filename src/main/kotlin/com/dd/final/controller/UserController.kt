package com.dd.final.controller

import com.dd.final.dto.AddUserRequest
import com.dd.final.dto.AuthRequest
import com.dd.final.dto.MessageResponse
import com.dd.final.security.error.BadRequestException
import com.dd.final.security.error.ValidationErrorProcessor
import com.dd.final.service.CustomUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UserController(
    private val customUserService: CustomUserService,
    private val validationErrorProcessor: ValidationErrorProcessor
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody authRequest: AuthRequest): ResponseEntity<Any> {
        return try {
            customUserService.login(authRequest)
        } catch (e: Exception) {
            val status = when (e) {
                is UsernameNotFoundException -> HttpStatus.BAD_REQUEST
                is BadCredentialsException -> HttpStatus.UNAUTHORIZED
                is BadRequestException -> HttpStatus.FORBIDDEN
                else -> HttpStatus.INTERNAL_SERVER_ERROR
            }
            ResponseEntity<Any>(MessageResponse(e.message), status)
        }
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody addUserRequest: AddUserRequest, result: BindingResult): ResponseEntity<Any> {
        if (result.hasFieldErrors()) {
            val errorMessage = validationErrorProcessor.process(result)
            return ResponseEntity.badRequest().body(MessageResponse(errorMessage))
        }
        return try {
            customUserService.register(addUserRequest)
        } catch (e: BadRequestException) {
            return ResponseEntity.badRequest().body(MessageResponse(e.message))
        }
    }
}