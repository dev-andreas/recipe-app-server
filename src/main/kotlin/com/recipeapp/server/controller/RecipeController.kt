package com.recipeapp.server.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.controller.model.RecipeModel
import com.recipeapp.server.controller.model.toNewDBModel
import com.recipeapp.server.repository.RecipeRepository
import com.recipeapp.server.repository.model.Recipe
import com.recipeapp.server.repository.model.toModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recipe")
class RecipeController(
    val repository: RecipeRepository
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handeNotFound(e: NoSuchElementException): ResponseEntity<String> = ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @GetMapping("/")
    fun readAll(): List<RecipeModel> = repository.findAll().map { it.toModel() }

    @GetMapping("/{id}")
    fun read(@PathVariable id: Long): RecipeModel = repository.findById(id).get().toModel()

    @PostMapping("/")
    fun create(@RequestBody recipe: RecipeModel) = repository.save(recipe.toNewDBModel())

    @PutMapping("/")
    fun update(@RequestBody recipe: RecipeModel) {
        val dbModel: Recipe = repository.findById(recipe.id).get()
        dbModel.name = recipe.name
        dbModel.type = recipe.type
        dbModel.instructions = recipe.instructions
        dbModel.ingredients = jacksonObjectMapper().writeValueAsString(recipe.ingredients)
        repository.save(dbModel)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}