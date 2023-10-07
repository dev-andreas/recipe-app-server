package com.recipeapp.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.recipeapp.server.repository.UserRepository
import com.recipeapp.server.repository.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    val userRepository: UserRepository
) {

    @Value("\${security.jwt.private-key}")
    lateinit var secret: String
    @Value("\${security.jwt.access-expiration}")
    lateinit var accessExpiration: String
    @Value("\${security.jwt.refresh-expiration}")
    lateinit var refreshExpiration: String

    private fun getExpiration(jwtType: JwtType): Long {
        return when (jwtType) {
            JwtType.ACCESS -> accessExpiration.toLong()
            JwtType.REFRESH -> refreshExpiration.toLong()
        }
    }

    fun createToken(user: User, jwtType: JwtType, issuedAt: Date = Date()): String {
        val algorithm = Algorithm.HMAC512(secret)

        return JWT.create()
            .withIssuer(user.email)
            .withClaim("type", jwtType.type)
            .withIssuedAt(issuedAt)
            .withExpiresAt(Date(System.currentTimeMillis() + getExpiration(jwtType)))
            .sign(algorithm)
    }

    fun verify(token: String, jwtType: JwtType): Optional<User> {
        val algorithm = Algorithm.HMAC512(secret)

        return try {
            val jwt = JWT.require(algorithm)
                .build()
                .verify(token)
            val email = jwt.issuer
            val type = jwt.getClaim("type").asString()
            val user = userRepository.findByEmail(email).orElseThrow()
            if (type == jwtType.type) {
                if (user.tokenValidAt.before(jwt.issuedAt)) {
                    Optional.of(user)
                } else {
                    Optional.empty()
                }
            } else {
                Optional.empty()
            }

        } catch (e: JWTVerificationException) {
            Optional.empty()
        }
    }
}