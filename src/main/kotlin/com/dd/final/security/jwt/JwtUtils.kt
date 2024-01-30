package com.dd.final.security.jwt

import com.dd.final.security.UserDetailImpl
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


@Component
class JwtUtils {
    @Value("\${app.jwtSecret}")
    lateinit var jwtSecret: String

    @Value("\${app.jwtExpirationMs}")
    var jwtExpirationMs :Int?=null

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserDetailImpl
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        val key: Key = Keys.hmacShaKeyFor(keyBytes)
        val token = Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs!!))
            .signWith(key).compact()
        return "Bearer $token"
    }

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).body.subject
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken)
            return true
        } catch (e: SecurityException) {
            LOG.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            LOG.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            LOG.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            LOG.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            LOG.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }
}