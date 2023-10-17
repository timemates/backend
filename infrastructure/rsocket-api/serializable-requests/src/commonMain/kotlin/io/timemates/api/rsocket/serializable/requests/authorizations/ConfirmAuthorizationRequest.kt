package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.authorization.SerializableAuthorization
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmAuthorizationRequest(
    val verificationHash: String,
    val confirmationCode: String,
) : RSocketRequest<ConfirmAuthorizationRequest.Result> {
    companion object Key : RSocketRequest.Key<ConfirmAuthorizationRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data class Result(
        val isNewAccount: Boolean,
        val authorization: SerializableAuthorization?,
    )
}