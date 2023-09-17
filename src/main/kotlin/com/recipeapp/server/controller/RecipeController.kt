package com.recipeapp.server.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.recipeapp.server.controller.response.ErrorResponse
import com.recipeapp.server.controller.model.RecipeModel
import com.recipeapp.server.repository.RecipeRepository
import com.recipeapp.server.repository.model.Recipe
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/recipe")
@PreAuthorize("hasRole('RECIPE_USER')")
class RecipeController(
    val repository: RecipeRepository
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handeNoSuchElement(e: NoSuchElementException) = ResponseEntity(ErrorResponse("Recipe not found."), HttpStatus.NOT_FOUND)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParse(e: HttpMessageNotReadableException) = ResponseEntity(ErrorResponse("Couldn't parse JSON."), HttpStatus.BAD_REQUEST)

    @GetMapping("")
    @PreAuthorize("hasAuthority('recipe:read')")
    fun readAll(): List<RecipeModel> = repository.findAll().map { it.toModel() }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recipe:read')")
    fun read(@PathVariable id: Long): RecipeModel = repository.findById(id).orElseThrow().toModel()

    @PostMapping("")
    @PreAuthorize("hasAuthority('recipe:create')")
    fun create(@RequestBody recipe: RecipeModel) = repository.save(recipe.toNewDBModel()).toModel()

    @PutMapping("")
    @PreAuthorize("hasAuthority('recipe:update')")
    fun update(@RequestBody recipe: RecipeModel): RecipeModel {
        val result = repository.findById(recipe.id)

        val dbModel: Recipe = result.orElseThrow()
        dbModel.name = recipe.name
        dbModel.type = recipe.type
        dbModel.instructions = recipe.instructions
        dbModel.ingredients = jacksonObjectMapper().writeValueAsString(recipe.ingredients)
        return repository.save(dbModel).toModel()
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('recipe:delete')")
    fun delete(@PathVariable id: Long): RecipeModel {
        val dbModel = repository.findById(id).orElseThrow().toModel()
        repository.deleteById(id)
        return dbModel
    }
}