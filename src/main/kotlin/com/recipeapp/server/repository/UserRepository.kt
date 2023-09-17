package com.recipeapp.server.repository

import com.recipeapp.server.repository.model.User
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface UserRepository : CrudRepository<User, Long> {

    fun findByEmail(email: String): Optional<User>;
}