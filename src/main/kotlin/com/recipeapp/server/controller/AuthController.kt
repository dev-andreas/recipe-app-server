package com.recipeapp.server.controller

import com.recipeapp.server.controller.request.LoginRequest
import com.recipeapp.server.controller.request.RegisterRequest
import com.recipeapp.server.controller.response.ErrorResponse
import com.recipeapp.server.controller.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val authService: AuthService
) {

    @ExceptionHandler(ResponseStatusException::class)
    fun handeResponseStatus(e: ResponseStatusException) = ResponseEntity(ErrorResponse(e.reason ?: ""), e.statusCode)

    @ExceptionHandler(NoSuchElementException::class)
    fun handeNoSuchElement(e: NoSuchElementException) = ResponseEntity(ErrorResponse(ErrorResponse.USER_NOT_FOUND), HttpStatus.NOT_FOUND)

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(e: AuthenticationException) = ResponseEntity(ErrorResponse(ErrorResponse.USER_NOT_AUTHENTICATED), HttpStatus.UNAUTHORIZED)

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest) = authService.register(registerRequest)

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest) = authService.login(loginRequest)

    @PostMapping("/logout")
    fun logout() = authService.logout()

    @PostMapping("/refresh")
    fun refresh(request: HttpServletRequest) = authService.refresh(request)
}