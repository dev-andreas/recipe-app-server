package com.recipeapp.server.controller

import com.recipeapp.server.controller.response.ErrorResponse
import com.recipeapp.server.controller.model.RecipeModel
import com.recipeapp.server.controller.service.RecipeService
import com.recipeapp.server.repository.RecipeRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/recipe")
@PreAuthorize("hasRole('RECIPE_USER')")
class RecipeController(
    val repository: RecipeRepository,
    val recipeService: RecipeService
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handeNoSuchElement(e: NoSuchElementException) = ResponseEntity(ErrorResponse("Recipe not found."), HttpStatus.NOT_FOUND)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParse(e: HttpMessageNotReadableException) = ResponseEntity(ErrorResponse("Couldn't parse JSON."), HttpStatus.BAD_REQUEST)

    @GetMapping("")
    @PreAuthorize("hasAuthority('recipe:read')")
    fun readAll() = recipeService.readAll()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recipe:read')")
    fun read(@PathVariable id: Long) = recipeService.read(id)

    @PostMapping("")
    @PreAuthorize("hasAuthority('recipe:create')")
    fun create(@RequestBody recipe: RecipeModel) = recipeService.create(recipe)

    @PutMapping("")
    @PreAuthorize("hasAuthority('recipe:update')")
    fun update(@RequestBody recipe: RecipeModel) = recipeService.update(recipe)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('recipe:delete')")
    fun delete(@PathVariable id: Long) = recipeService.delete(id)
}