package com.recipeapp.server.controller.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.repository.model.Recipe
import com.recipeapp.server.repository.model.User
import org.springframework.security.core.context.SecurityContextHolder

data class RecipeModel(
    val id: Long,
    val name: String,
    val type: Type,
    val instructions: String,
    val ingredients: List<Ingredient>,
) {
    data class Ingredient(
        val name: String,
        val amount: Int,
        val unit: String
    )

    enum class Type {
        BREAKFAST,
        LUNCH,
        DINNER
    }

    fun toNewDBModel() = Recipe(
        name = this.name,
        type = this.type,
        instructions = this.instructions,
        ingredients = jacksonObjectMapper().writeValueAsString(this.ingredients),
        user = SecurityContextHolder.getContext().authentication.principal as User
    )
}