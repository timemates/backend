package io.timemates.backend.rsocket.features.authorization.requests

import io.timemates.backend.serializable.types.authorization.SerializableClientMetadata
import kotlinx.serialization.Serializable

@Serializable
data class StartAuthorizationRequest(
    val email: String,
    val clientMetadata: SerializableClientMetadata,
) {
    @Serializable
    data class Result(
        val verificationHash: String,
        val expiresAt: Long,
        val attempts: Int,
    )
}