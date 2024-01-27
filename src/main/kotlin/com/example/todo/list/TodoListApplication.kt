package com.example.todo.list

import com.example.todo.list.model.ApplicationUser
import com.example.todo.list.model.ApplicationUserRole
import com.example.todo.list.model.UserRole
import com.example.todo.list.repository.UserRepository
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Component

@SpringBootApplication
@EnableJpaRepositories
class TodoListApplication

fun main(args: Array<String>) {
    runApplication<TodoListApplication>(*args)
}

@Component
public class RunAfterStartup(private val userRepository: UserRepository) {

    @EventListener(
        ApplicationReadyEvent::class
    )
    fun runAfterStartup() {

        userRepository.save(
            ApplicationUser(
                id = 1,
                "111",
                "111",
                roles = setOf(UserRole(name = ApplicationUserRole.ROLE_GOD))
            )
        )
        println("Gotovo")
    }
}