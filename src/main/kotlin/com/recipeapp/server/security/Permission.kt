package com.recipeapp.server.security

enum class Permission(
    val permission: String
) {
    RECIPE_CREATE("recipe:create"),
    RECIPE_READ("recipe:read"),
    RECIPE_UPDATE("recipe:update"),
    RECIPE_DELETE("recipe:delete")
}