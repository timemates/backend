package io.timemates.backend.rsocket.features.authorization.requests

import io.timemates.backend.serializable.types.authorization.SerializableAuthorization
import kotlinx.serialization.Serializable

@Serializable
data class ConfigureAccountRequest(
    val verificationHash: String,
    val name: String,
    val description: String?,
) {
    @Serializable
    data class Result(
        val authorization: SerializableAuthorization,
    )
}