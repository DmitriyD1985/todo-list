package com.example.todo.list.service

import com.example.todo.list.dto.LoginDto
import com.example.todo.list.dto.LogonDto
import com.example.todo.list.model.ApplicationUser
import com.example.todo.list.model.UserRole
import com.example.todo.list.repository.RoleRepository
import com.example.todo.list.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val customUserDetailsService: CustomUserDetailsService,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {
    fun loginUser(loginDto: LoginDto): ApplicationUser {
        val user = userRepository.findByUsername(loginDto.name)
        if (user == null) {
            throw Exception("Пользователь не существует")
        } else if (user.password == loginDto.password) {
            customUserDetailsService.loadUserByUsername(user.username)
        }
        return user
    }

    fun logonUser(logonDto: LogonDto): ApplicationUser {

        if (userRepository.findByUsername(logonDto.userName) != null) {
            throw Exception("Пользователь не существует")
        }

        val appUser: ApplicationUser = logonDto.toApplicationUser()


        val roles: Set<UserRole> = roleRepository.findAllByName(logonDto.role)
        appUser.roles = roles

        return userRepository.save(appUser)
    }

}