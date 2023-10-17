package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.types.authorization.SerializableAuthorization
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