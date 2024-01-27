package com.example.todo.list.repository

import com.example.todo.list.model.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<UserRole, Long> {
    fun findAllByName(name: String): Set<UserRole>
}