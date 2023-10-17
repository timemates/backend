package io.timemates.api.rsocket.serializable.requests.users

import io.timemates.api.rsocket.serializable.types.users.SerializableAvatar
import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val avatar: SerializableAvatar?,
    val name: String?,
    val description: String?,
)