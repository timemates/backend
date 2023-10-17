package io.timemates.api.rsocket.serializable.requests.users

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class EditEmailRequest(
    val email: String,
) : RSocketRequest<EditEmailRequest.Result> {
    companion object Key : RSocketRequest.Key<EditEmailRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data object Result
}