package com.recipeapp.server.controller.model

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.repository.model.Recipe

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

    enum class Type(@JsonValue val value: String) {
        BREAKFAST("Breakfast"),
        LUNCH("Lunch"),
        DINNER("Dinner")
    }
}

fun RecipeModel.toNewDBModel() = Recipe(
    name = this.name,
    type = this.type.value,
    instructions = this.instructions,
    ingredients = jacksonObjectMapper().writeValueAsString(this.ingredients)
)