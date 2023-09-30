package com.recipeapp.server.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.controller.response.ErrorResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtFilter(
    val jwtService: JwtService
) : OncePerRequestFilter() {

    val shouldNotFilterUrls = listOf(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/api/v1/auth/refresh",
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.status = 401
            response.writer.write(jacksonObjectMapper().writeValueAsString(ErrorResponse("No bearer token provided.")))
            return
        }

        val jwt = authHeader.substring(7)
        val verification = jwtService.verify(jwt, JwtType.ACCESS)
        if (verification.isPresent) {
            val user = verification.get()
            SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                user,
                null,
                user.authorities
            )
        } else {
            response.status = 401
            response.writer.write(jacksonObjectMapper().writeValueAsString(ErrorResponse("Bearer token is invalid.")))
            return
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return shouldNotFilterUrls.map { path.startsWith(it) }.any { it }
    }
}