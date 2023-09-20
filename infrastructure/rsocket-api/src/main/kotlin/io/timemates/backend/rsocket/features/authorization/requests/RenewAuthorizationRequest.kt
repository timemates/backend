package io.timemates.backend.rsocket.features.authorization.requests

import kotlinx.serialization.Serializable

@Serializable
data class RenewAuthorizationRequest(
    val refreshHash: String,
) {
    @Serializable
    data class Result(val accessHash: String)
}