package com.recipeapp.server.repository.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.recipeapp.server.controller.model.RecipeModel
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
class Recipe(
    @Column(length = 64)
    var name: String,

    @Enumerated(EnumType.STRING)
    var type: RecipeModel.Type,

    @Column(columnDefinition = "TEXT")
    var instructions: String,

    @Column(columnDefinition = "TEXT")
    var ingredients: String,

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    var user: User
) {
    @Id
    @GeneratedValue
    var id: Long? = null

    fun toModel() = RecipeModel(
        id = this.id ?: 0,
        name = this.name,
        type = this.type,
        instructions = this.instructions,
        ingredients = jacksonObjectMapper().readValue(this.ingredients),
    )
}