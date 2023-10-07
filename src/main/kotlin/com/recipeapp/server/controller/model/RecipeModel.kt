package com.recipeapp.server.controller.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.repository.model.Recipe
import com.recipeapp.server.repository.model.User
import org.owasp.encoder.Encode.forHtml
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
        name = forHtml(this.name),
        type = this.type,
        instructions = forHtml(this.instructions),
        ingredients = forHtml(jacksonObjectMapper().writeValueAsString(this.ingredients)),
        user = SecurityContextHolder.getContext().authentication.principal as User
    )
}