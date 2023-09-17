package com.recipeapp.server.controller.request

data class LoginRequest(
    val email: String,
    val password: String
)