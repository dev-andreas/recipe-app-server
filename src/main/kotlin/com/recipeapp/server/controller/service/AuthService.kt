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
    val authenticationManager: AuthenticationManager
) {

    private fun checkPasswordStrength(password: String): String {
        if (password.trim() == "") {
            return "Password must not be empty"
        }
        if (password.length < 8) {
            return "Password must be at least 8 characters"
        }

        if (!"[0-9]".toRegex().containsMatchIn(password)) {
            return "Password must contain at least one numeric character"
        }

        if (!"[\\§\\\$\\%\\&\\/\\=\\\\\\+\\#\\-\\_\\.]".toRegex().containsMatchIn(password)) {
            return "Password must contain at least one of the following characters: §$%&/=\\+#-_."
        }

        if (!"[0-9a-zA-Z\\§\\\$\\%\\&\\/\\=\\\\\\+\\#\\-\\_\\.]*".toRegex().matches(password)) {
            return "Password can only contain alphanumeric characters and §$%&/=\\+#-_."
        }

        return ""
    }

    fun register(registerRequest: RegisterRequest) {

        if (userRepository.findByEmail(registerRequest.email).isPresent) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "E-Mail already exists")
        }

        if (registerRequest.email.trim() == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "E-Mail must not be empty")
        }

        if (!"^\\S+@\\S+$".toRegex().matches(registerRequest.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "E-Mail is invalid")
        }

        if (registerRequest.lastName.trim() == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name must not be empty")
        }

        if (registerRequest.firstName.trim() == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "First name must not be empty")
        }

        val passwordStrength = checkPasswordStrength(registerRequest.password)

        if (passwordStrength != "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, passwordStrength)
        }

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