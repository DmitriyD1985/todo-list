package com.example.todo.list.service

import com.example.todo.list.exception.ApplicationException
import com.example.todo.list.repository.RecordRepository
import com.example.todo.list.model.RecordEntity
import kotlin.jvm.optionals.getOrElse
import org.springframework.stereotype.Service

@Service
class RecordService(
    private val recordRepository: RecordRepository
) {
    fun create(recordEntity: RecordEntity) = recordRepository.save(recordEntity)

    fun read(name: String) =
        recordRepository.findByName(name)

    fun update(recordEntity: RecordEntity, id: Long) =
        recordRepository.findById(id).getOrElse { throw ApplicationException("record for update not found") }
            .let { recordRepository.save(RecordEntity(id, recordEntity.name)) }

    fun delete(id: Long) = recordRepository.deleteById(id)
}