package com.recipeapp.server.controller.service

import com.recipeapp.server.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FormService(
    val userRepository: UserRepository
) {
    fun validatePassword(password: String, fieldName: String = "Password") {
        validateNotEmpty(password, fieldName)

        if (password.length < 8) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName must be at least 8 characters")
        }

        if (!"[0-9]".toRegex().containsMatchIn(password)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName must contain at least one numeric character")
        }

        if (!"[\\§\\\$\\%\\&\\/\\=\\\\\\+\\#\\-\\_\\.]".toRegex().containsMatchIn(password)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName must contain at least one of the following characters: §$%&/=\\+#-_.")
        }

        if (!"[0-9a-zA-Z\\§\\\$\\%\\&\\/\\=\\\\\\+\\#\\-\\_\\.]*".toRegex().matches(password)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName can only contain alphanumeric characters and §$%&/=\\+#-_.")
        }
    }

    fun validateEmail(email: String, fieldName: String = "E-Mail") {
        validateNotEmpty(email, fieldName)

        if (!"^\\S+@\\S+$".toRegex().matches(email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName is invalid")
        }
    }

    /**
     * This method additionally validates that the given new email is not taken by another user.
     * @param email The email to validate.
     * @param fieldName The name of the form input. Used for error messages.
     */
    fun validateNewEmail(email: String, fieldName: String = "E-Mail") {
        validateEmail(email, fieldName = fieldName)
        if (userRepository.findByEmail(email).isPresent) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName already exists")
        }
    }

    /**
     * This method additionally validates that the given existing email is not taken by another user except the user that is currently associated with the email.
     * @param email The email to validate.
     * @param username The username of the user that is associated with this email.
     * @param fieldName The name of the form input. Used for error messages.
     */
    fun validateExistingEmail(email: String, username: String, fieldName: String = "E-Mail") {
        validateEmail(email, fieldName = fieldName)
        val user = userRepository.findByEmail(email)
        if (user.isPresent && user.get().username != username) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName already exists")
        }
    }

    fun validateNotEmpty(field: String, fieldName: String) {
        if (field.trim() == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$fieldName must not be empty")
        }
    }
}