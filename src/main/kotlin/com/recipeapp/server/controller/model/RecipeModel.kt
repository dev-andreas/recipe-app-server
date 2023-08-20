package com.recipeapp.server.controller.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.repository.model.Recipe

data class RecipeModel(
    val id: Long,
    val name: String,
    val type: String,
    val instructions: String,
    val ingredients: List<Ingredient>
) {
    data class Ingredient(
        val name: String,
        val amount: Int,
        val unit: String
    )
}

fun RecipeModel.toNewDBModel() = Recipe(
    name = this.name,
    type = this.type,
    instructions = this.instructions,
    ingredients = jacksonObjectMapper().writeValueAsString(this.ingredients)
)