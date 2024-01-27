package com.example.todo.list.repository

import com.example.todo.list.model.ApplicationUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<ApplicationUser, Long>{
    fun findByUsername(username: String): ApplicationUser?
    fun existsByUsername(username: String): Boolean
}