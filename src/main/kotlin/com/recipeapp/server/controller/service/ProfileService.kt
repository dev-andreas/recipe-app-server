package com.recipeapp.server.controller.service

import com.recipeapp.server.controller.request.ChangePasswordRequest
import com.recipeapp.server.controller.request.EditUserRequest
import com.recipeapp.server.repository.UserRepository
import com.recipeapp.server.repository.model.User
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProfileService(
    val repository: UserRepository,
    val passwordEncoder: BCryptPasswordEncoder,
    val formService: FormService,
    val authService: AuthService
) {

    fun editUser(editUserRequest: EditUserRequest) {
        val user: User = SecurityContextHolder.getContext().authentication.principal as User

        formService.validateExistingEmail(editUserRequest.email, user.username)
        formService.validateNotEmpty(editUserRequest.lastName, "Last name")
        formService.validateNotEmpty(editUserRequest.firstName, "First name")

        if (user.email != editUserRequest.email) {
            authService.logout()
        }

        user.firstName = editUserRequest.firstName
        user.lastName = editUserRequest.lastName
        user.email = editUserRequest.email
        repository.save(user)
    }

    fun changePassword(changePasswordRequest: ChangePasswordRequest) {
        val user: User = SecurityContextHolder.getContext().authentication.principal as User

        // checking old password
        if (!passwordEncoder.matches(changePasswordRequest.oldPassword, user.passw)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect")
        }

        formService.validatePassword(changePasswordRequest.newPassword)

        user.passw = passwordEncoder.encode(changePasswordRequest.newPassword)
        repository.save(user)
    }
}