package io.timemates.backend.rsocket.features.authorization.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetAuthorizationsRequest(
    val pageToken: String? = null,
)