package com.example.todo.list.repository

import com.example.todo.list.model.RecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecordRepository: JpaRepository<RecordEntity, Long>{
    fun findByName(name: String): RecordEntity?
}