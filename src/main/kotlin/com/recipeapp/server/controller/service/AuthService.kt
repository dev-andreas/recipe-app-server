package com.recipeapp.server.controller.service

import com.recipeapp.server.controller.request.LoginRequest
import com.recipeapp.server.controller.request.RegisterRequest
import com.recipeapp.server.controller.response.ErrorResponse
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
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*


@Service
class AuthService(
    val userRepository: UserRepository,
    val passwordEncoder: BCryptPasswordEncoder,
    val jwtService: JwtService,
    val authenticationManager: AuthenticationManager,
    val formService: FormService
) {

    fun register(registerRequest: RegisterRequest) {
        formService.validateNewEmail(registerRequest.email)
        formService.validateNotEmpty(registerRequest.lastName, "Last name")
        formService.validateNotEmpty(registerRequest.firstName, "First name")
        formService.validatePassword(registerRequest.password)

        userRepository.save(
            User(
                email = registerRequest.email,
                firstName = registerRequest.firstName,
                lastName = registerRequest.lastName,
                passw = passwordEncoder.encode(registerRequest.password),
                role = Role.RECIPE_USER,
                tokenValidAt = Date()
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

        val loginDate = Date()
        user.tokenValidAt = loginDate
        userRepository.save(user)

        val calendar = Calendar.getInstance()
        calendar.time = loginDate
        calendar.add(Calendar.SECOND, 1)

        val accessToken = jwtService.createToken(user, JwtType.ACCESS, calendar.time)
        val refreshToken = jwtService.createToken(user, JwtType.REFRESH, calendar.time)

        return LoginResponse(accessToken, refreshToken, user.email, user.firstName, user.lastName)
    }

    fun logout() {
        val user: User = SecurityContextHolder.getContext().authentication.principal as User
        user.tokenValidAt = Date()
        userRepository.save(user)
    }

    fun refresh(request: HttpServletRequest): RefreshResponse {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorResponse.BEARER_TOKEN_INVALID)
        }

        val jwt = authHeader.substring(7)
        val verification = jwtService.verify(jwt, JwtType.REFRESH)

        if (verification.isEmpty) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorResponse.BEARER_TOKEN_INVALID)
        }
        val user = verification.get()
        return RefreshResponse(jwtService.createToken(user, JwtType.ACCESS))
    }
}