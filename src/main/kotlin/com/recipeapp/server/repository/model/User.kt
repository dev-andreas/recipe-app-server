package com.recipeapp.server.repository.model

import com.recipeapp.server.security.Role
import jakarta.persistence.*
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "api_user")
class User(

    @Column(length = 64, unique = true)
    var email: String,

    @Column(length = 32)
    var firstName: String,

    @Column(length = 32)
    var lastName: String,

    @Column(length = 128)
    var passw: String,

    @Enumerated(EnumType.STRING)
    var role: Role

) : UserDetails {
    @Id
    @GeneratedValue
    var id: Long? = null

    override fun getAuthorities() = role.getAuthorities()

    override fun getPassword() = passw

    override fun getUsername() = email

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}