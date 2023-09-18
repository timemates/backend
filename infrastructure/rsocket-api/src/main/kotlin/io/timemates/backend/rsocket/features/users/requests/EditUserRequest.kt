package io.timemates.backend.rsocket.features.users.requests

import io.timemates.backend.serializable.types.users.SerializableAvatar
import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val avatar: SerializableAvatar?,
    val name: String?,
    val description: String?,
)