package com.recipeapp.server.controller.request

data class RegisterRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)