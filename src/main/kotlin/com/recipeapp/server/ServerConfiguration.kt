package com.recipeapp.server

import com.recipeapp.server.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebMvc
class ServerConfiguration(
    val userRepository: UserRepository
) {

    @Value("\${cors-allowed-origins}")
    lateinit var corsAllowedOrigins: String

    @Bean
    fun addCorsConfig(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                val corsAllowedOriginsArray = corsAllowedOrigins.split(",").toTypedArray()
                registry.addMapping("/**")
                    .allowedMethods("*")
                    .allowedOriginPatterns(*corsAllowedOriginsArray)
                    .allowCredentials(true)
            }
        }
    }

    @Bean
    fun userDetailsService() = UserDetailsService {
        userRepository.findByEmail(it)
            .orElseThrow { UsernameNotFoundException("User not found.") }
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
}