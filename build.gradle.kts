import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.4"
	kotlin("jvm") version "1.8.22"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("io.github.microutils:kotlin-logging:3.0.5")
	implementation("com.auth0:java-jwt:4.4.0")
	implementation("io.jsonwebtoken:jjwt-api:0.12.0")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.0")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.0")
	implementation("org.springframework.security.oauth:spring-security-oauth2:2.5.2.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
