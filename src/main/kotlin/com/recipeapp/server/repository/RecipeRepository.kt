package com.recipeapp.server.repository

import com.recipeapp.server.repository.model.Recipe
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RecipeRepository : CrudRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe as r where r.id = :id and r.user.id = :userId")
    fun findByIdAndUserId(id: Long, userId: Long): Optional<Recipe>


    @Query("SELECT r from Recipe as r where r.user.id = :userId")
    fun findAllByUserId(userId: Long): List<Recipe>
}