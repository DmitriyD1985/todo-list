package com.example.todo.list.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils {
    @Value("\${app.jwtSecret}")
    private lateinit var jwtSecret: String

    @Value("\${app.jwtExpirationMs}")
    private lateinit var jwtExpirationMs: String

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetails
        return Jwts.builder().setSubject(userPrincipal.username).setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs.toLong()))
            .signWith(SignatureAlgorithm.HS512, jwtSecret).compact()
    }

    fun validateJwtToken(jwt: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(jwt)
            return true
        } catch (e: MalformedJwtException) {
            System.err.println(e.message)
        } catch (e: IllegalArgumentException) {
            System.err.println(e.message)
        }
        return false
    }

    fun getUserNameFromJwtToken(jwt: String?): String {
        return Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(jwt).getBody().getSubject()
    }
}