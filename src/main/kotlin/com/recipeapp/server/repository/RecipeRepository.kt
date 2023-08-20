package com.recipeapp.server.repository

import com.recipeapp.server.repository.model.Recipe
import org.springframework.data.repository.CrudRepository

interface RecipeRepository : CrudRepository<Recipe, Long> {
}