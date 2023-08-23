package com.recipeapp.server

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class ServerConfiguration {

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
}