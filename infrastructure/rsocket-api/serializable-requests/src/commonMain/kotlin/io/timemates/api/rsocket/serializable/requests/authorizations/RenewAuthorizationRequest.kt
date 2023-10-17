package io.timemates.api.rsocket.serializable.requests.authorizations

import kotlinx.serialization.Serializable

@Serializable
data class RenewAuthorizationRequest(
    val refreshHash: String,
) {
    @Serializable
    data class Result(val accessHash: String)
}