package com.dd.final.security.jwt

import mu.KotlinLogging
import java.io.IOException

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component


@Component
class AuthEntryPointJwt : AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest?, response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        LOG.error("Unauthorized error: {}", authException.message)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized")
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }
}