package com.recipeapp.server.controller.request

data class EditUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String
)