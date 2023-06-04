package io.timemates.backend.application.dependencies.configuration

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String?,
)