package com.recipeapp.server.security

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role(
    private val permissions: Set<Permission>
) {
    NONE(setOf()),
    RECIPE_USER(
        setOf(
            Permission.RECIPE_CREATE,
            Permission.RECIPE_READ,
            Permission.RECIPE_UPDATE,
            Permission.RECIPE_DELETE,
            Permission.PROFILE_EDIT,
            Permission.PROFILE_CHANGE_PASSWORD
        )
    );

    fun getAuthorities() =
        (permissions.map { SimpleGrantedAuthority(it.permission) }.toMutableList() +
                mutableListOf(SimpleGrantedAuthority("ROLE_${this.name}")))
}