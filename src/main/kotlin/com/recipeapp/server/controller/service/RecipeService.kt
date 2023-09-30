package com.recipeapp.server.controller.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.controller.model.RecipeModel
import com.recipeapp.server.repository.RecipeRepository
import com.recipeapp.server.repository.model.Recipe
import com.recipeapp.server.repository.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class RecipeService(
    val recipeRepository: RecipeRepository
) {

    private fun getUser() = SecurityContextHolder.getContext().authentication.principal as User

    fun readAll() = recipeRepository.findAllByUserId(getUser().id ?: 0).map { it.toModel() }

    fun read(id: Long) = recipeRepository.findByIdAndUserId(id, getUser().id ?: 0).orElseThrow().toModel()

    fun create(recipe: RecipeModel) = recipeRepository.save(recipe.toNewDBModel()).toModel()

    fun update(recipe: RecipeModel): RecipeModel {
        val result = recipeRepository.findByIdAndUserId(recipe.id, getUser().id ?: 0)

        val dbModel: Recipe = result.orElseThrow()
        dbModel.name = recipe.name
        dbModel.type = recipe.type
        dbModel.instructions = recipe.instructions
        dbModel.ingredients = jacksonObjectMapper().writeValueAsString(recipe.ingredients)
        return recipeRepository.save(dbModel).toModel()
    }

    fun delete(id: Long): RecipeModel {
        val dbModel = recipeRepository.findByIdAndUserId(id, getUser().id ?: 0).orElseThrow().toModel()
        recipeRepository.deleteById(id)
        return dbModel
    }
}