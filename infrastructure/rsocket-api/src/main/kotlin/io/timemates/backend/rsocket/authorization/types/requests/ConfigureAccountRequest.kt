package io.timemates.backend.rsocket.authorization.types.requests

import kotlinx.serialization.Serializable

@Serializable
data class ConfigureAccountRequest(
    val verificationHash: String,
    val name: String,
    val description: String?,
)