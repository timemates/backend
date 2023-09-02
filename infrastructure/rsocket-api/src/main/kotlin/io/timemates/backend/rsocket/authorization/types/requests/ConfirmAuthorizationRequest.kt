package io.timemates.backend.rsocket.authorization.types.requests

import io.timemates.backend.rsocket.authorization.types.Authorization
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmAuthorizationRequest(
    val verificationHash: String,
    val confirmationCode: String,
) {
    data class Response(
        val isNewAccount: Boolean,
        val authorization: Authorization?,
    )
}