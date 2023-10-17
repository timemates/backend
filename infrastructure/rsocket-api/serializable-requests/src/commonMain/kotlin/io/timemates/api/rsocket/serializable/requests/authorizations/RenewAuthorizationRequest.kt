package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class RenewAuthorizationRequest(
    val refreshHash: String,
) : RSocketRequest<RenewAuthorizationRequest.Result> {
    companion object Key : RSocketRequest.Key<RenewAuthorizationRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data class Result(val accessHash: String)
}