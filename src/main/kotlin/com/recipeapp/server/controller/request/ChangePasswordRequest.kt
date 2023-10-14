package com.recipeapp.server.controller.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)