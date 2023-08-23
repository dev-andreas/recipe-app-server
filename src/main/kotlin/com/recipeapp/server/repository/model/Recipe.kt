package com.recipeapp.server.repository.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.recipeapp.server.controller.model.RecipeModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Lob
import org.hibernate.annotations.JdbcType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.repository.init.ResourceReader

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
    type = this.type,
    instructions = this.instructions,
    ingredients = jacksonObjectMapper().readValue(this.ingredients)
)