package com.dd.final.repository

import com.dd.final.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun save(user: User): User
    fun findByUsername(username: String?): User?
    fun existsByUsername(username: String): Boolean
}