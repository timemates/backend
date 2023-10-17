package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.types.authorization.SerializableAuthorization
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