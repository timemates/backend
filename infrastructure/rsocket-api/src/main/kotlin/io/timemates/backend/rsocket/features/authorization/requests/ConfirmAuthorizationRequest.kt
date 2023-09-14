package io.timemates.backend.rsocket.features.authorization.requests

import io.timemates.backend.serializable.types.authorization.SerializableAuthorization
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmAuthorizationRequest(
    val verificationHash: String,
    val confirmationCode: String,
) {
    data class Response(
        val isNewAccount: Boolean,
        val authorization: SerializableAuthorization?,
    )
}