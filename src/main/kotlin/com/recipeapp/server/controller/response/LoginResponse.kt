package com.recipeapp.server.controller.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val firstName: String,
    val lastName: String
)
