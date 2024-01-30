package com.dd.final

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.dd.final.repository"])
@EnableTransactionManagement
@EntityScan(basePackages = ["com.dd.final.model"])

class FinalApplication

fun main(args: Array<String>) {
	runApplication<FinalApplication>(*args)
}