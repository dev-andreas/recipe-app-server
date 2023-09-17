package com.recipeapp.server.controller.service

import com.recipeapp.server.controller.request.LoginRequest
import com.recipeapp.server.controller.request.RegisterRequest
import com.recipeapp.server.controller.response.LoginResponse
import com.recipeapp.server.controller.response.RefreshResponse
import com.recipeapp.server.repository.UserRepository
import com.recipeapp.server.repository.model.User
import com.recipeapp.server.security.JwtService
import com.recipeapp.server.security.JwtType
import com.recipeapp.server.security.Role
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    val userRepository: UserRepository,
    val passwordEncoder: BCryptPasswordEncoder,
    val jwtService: JwtService,
    val authenticationManager: AuthenticationManager
) {

    fun register(registerRequest: RegisterRequest) {
        if (userRepository.findByEmail(registerRequest.email).isPresent) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "E-Mail already exists.")
        }

        userRepository.save(
            User(
                email = registerRequest.email,
                firstName = registerRequest.firstName,
                lastName = registerRequest.lastName,
                passw = passwordEncoder.encode(registerRequest.password),
                role = Role.NONE
            )
        )
    }

    fun login(loginRequest: LoginRequest): LoginResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )
        val user = userRepository.findByEmail(loginRequest.email).orElseThrow()
        val accessToken = jwtService.createToken(user, JwtType.ACCESS)
        val refreshToken = jwtService.createToken(user, JwtType.REFRESH)

        return LoginResponse(accessToken, refreshToken)
    }

    fun refresh(request: HttpServletRequest): RefreshResponse {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No bearer token provided.")
        }

        val jwt = authHeader.substring(7)
        val verification = jwtService.verify(jwt, JwtType.REFRESH)

        if (verification.isEmpty) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bearer token is invalid.")
        }
        val user = verification.get()
        return RefreshResponse(jwtService.createToken(user, JwtType.ACCESS))
    }
}