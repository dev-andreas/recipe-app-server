package com.recipeapp.server.repository.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.recipeapp.server.controller.model.RecipeModel
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Recipe(
    var name: String,
    var type: String,
    var instructions: String,
    var ingredients: String
) {
    @Id
    @GeneratedValue
    var id: Long? = null
}

fun Recipe.toModel() = RecipeModel(
    id = this.id ?: 0,
    name = this.name,
    type = this.type,
    instructions = this.instructions,
    ingredients = jacksonObjectMapper().readValue(this.ingredients)
)