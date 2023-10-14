package com.recipeapp.server.controller

import com.recipeapp.server.controller.request.ChangePasswordRequest
import com.recipeapp.server.controller.request.EditUserRequest
import com.recipeapp.server.controller.response.ErrorResponse
import com.recipeapp.server.controller.service.ProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(
    val profileService: ProfileService
) {

    @ExceptionHandler(ResponseStatusException::class)
    fun handeResponseStatus(e: ResponseStatusException) = ResponseEntity(ErrorResponse(e.reason ?: ""), e.statusCode)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParse(e: HttpMessageNotReadableException) = ResponseEntity(ErrorResponse("Couldn't parse JSON."), HttpStatus.BAD_REQUEST)

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('profile:edit')")
    fun edit(@RequestBody request: EditUserRequest) = profileService.editUser(request)

    @PostMapping("/change-password")
    @PreAuthorize("hasAuthority('profile:change-password')")
    fun changePassword(@RequestBody request: ChangePasswordRequest) = profileService.changePassword(request)
}