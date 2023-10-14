package com.recipeapp.server.controller.response

class ErrorResponse(val error: String) {
    companion object {
        const val USER_NOT_FOUND = "User not found"
        const val USER_NOT_AUTHENTICATED = "User is not authenticated"

        const val RECIPE_NOT_FOUND = "Recipe not found"

        const val COULD_NOT_PARSE_JSON = "Could not parse JSON"
        const val NO_BEARER_TOKEN = "No bearer token provided"
        const val BEARER_TOKEN_INVALID = "Bearer token is invalid"
    }
}