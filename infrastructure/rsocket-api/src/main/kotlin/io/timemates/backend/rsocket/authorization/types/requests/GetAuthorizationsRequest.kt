package io.timemates.backend.rsocket.authorization.types.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetAuthorizationsRequest(
    val pageToken: String? = null,
)