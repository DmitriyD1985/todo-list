package com.example.todo.list.service

import com.example.todo.list.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,

) : UserDetailsService {

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String): UserDetails {
        val user = userRepository.findByUsername(userName).takeIf { it != null } ?: throw UsernameNotFoundException("User not found")
        return User(
            user.username,
            user.password,
            true,
            true,
            true,
            true,
            user.roles.map { it.name as GrantedAuthority }.toSet()
        )
    }
}