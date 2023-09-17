package com.recipeapp.server.controller.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
