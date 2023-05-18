package io.timemates.backend.application.dependencies.configuration

data class MailerConfig(
    val host: String,
    val port: Int,
    val user: String,
    val password: String?,
    val sender: String,
)