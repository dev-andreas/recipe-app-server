package com.recipeapp.server.security

enum class JwtType(val type: String) {
    ACCESS("access"),
    REFRESH("refresh")
}