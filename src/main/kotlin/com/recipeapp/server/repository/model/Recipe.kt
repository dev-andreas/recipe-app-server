package com.recipeapp.server.repository.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.recipeapp.server.controller.model.RecipeModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Recipe(
    @Column(length = 64)
    var name: String,

    @Column(length = 9)
    var type: String,

    @Column(columnDefinition = "TEXT")
    var instructions: String,

    @Column(columnDefinition = "TEXT")
    var ingredients: String
) {
    @Id
    @GeneratedValue
    var id: Long? = null
}

fun Recipe.toModel() = RecipeModel(
    id = this.id ?: 0,
    name = this.name,
    type = jacksonObjectMapper().readValue("\"${this.type}\""),
    instructions = this.instructions,
    ingredients = jacksonObjectMapper().readValue(this.ingredients)
)