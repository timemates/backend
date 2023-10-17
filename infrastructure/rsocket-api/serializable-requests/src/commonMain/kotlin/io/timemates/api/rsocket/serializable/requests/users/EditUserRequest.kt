package io.timemates.api.rsocket.serializable.requests.users

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.users.SerializableAvatar
import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val avatar: SerializableAvatar?,
    val name: String?,
    val description: String?,
) : RSocketRequest<EditUserRequest.Result> {
    companion object Key : RSocketRequest.Key<EditUserRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}