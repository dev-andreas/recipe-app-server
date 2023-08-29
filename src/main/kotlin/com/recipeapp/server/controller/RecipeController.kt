package com.recipeapp.server.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.controller.model.ErrorMessage
import com.recipeapp.server.controller.model.RecipeModel
import com.recipeapp.server.controller.model.toNewDBModel
import com.recipeapp.server.repository.RecipeRepository
import com.recipeapp.server.repository.model.Recipe
import com.recipeapp.server.repository.model.toModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/recipe")
class RecipeController(
    val repository: RecipeRepository
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handeNotFound(e: NoSuchElementException): ResponseEntity<ErrorMessage> = ResponseEntity(ErrorMessage(e.message ?: ""), HttpStatus.NOT_FOUND)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParse(e: HttpMessageNotReadableException): ResponseEntity<ErrorMessage> = ResponseEntity(ErrorMessage(e.message ?: ""), HttpStatus.BAD_REQUEST)

    @GetMapping("")
    fun readAll(): List<RecipeModel> = repository.findAll().map { it.toModel() }

    @GetMapping("/{id}")
    fun read(@PathVariable id: Long): RecipeModel {
        val result = repository.findById(id)
        if (!result.isPresent) {
            throw NoSuchElementException("No recipe exists with id $id.")
        }
        return result.get().toModel()
    }

    @PostMapping("")
    fun create(@RequestBody recipe: RecipeModel) = repository.save(recipe.toNewDBModel()).toModel()

    @PutMapping("")
    fun update(@RequestBody recipe: RecipeModel): RecipeModel {
        val result = repository.findById(recipe.id)

        if (!result.isPresent) {
            throw NoSuchElementException("No recipe exists with id ${recipe.id}.")
        }

        val dbModel: Recipe = result.get()
        dbModel.name = recipe.name
        dbModel.type = recipe.type.value
        dbModel.instructions = recipe.instructions
        dbModel.ingredients = jacksonObjectMapper().writeValueAsString(recipe.ingredients)
        return repository.save(dbModel).toModel()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): RecipeModel {
        val dbModel = repository.findById(id).get().toModel()
        repository.deleteById(id)
        return dbModel
    }
}