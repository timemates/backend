package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.authorization.SerializableClientMetadata
import kotlinx.serialization.Serializable

@Serializable
data class StartAuthorizationRequest(
    val email: String,
    val clientMetadata: SerializableClientMetadata,
) : RSocketRequest<StartAuthorizationRequest.Result> {
    companion object Key : RSocketRequest.Key<StartAuthorizationRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data class Result(
        val verificationHash: String,
        val expiresAt: Long,
        val attempts: Int,
    )
}