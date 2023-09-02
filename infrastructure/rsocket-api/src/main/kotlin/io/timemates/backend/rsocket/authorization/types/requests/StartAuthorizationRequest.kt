package io.timemates.backend.rsocket.authorization.types.requests

import io.timemates.backend.rsocket.authorization.types.ClientMetadata
import kotlinx.serialization.Serializable

@Serializable
data class StartAuthorizationRequest(
    val email: String,
    val clientMetadata: ClientMetadata,
)