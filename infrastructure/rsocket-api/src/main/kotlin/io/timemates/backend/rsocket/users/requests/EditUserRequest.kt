package io.timemates.backend.rsocket.users.requests

import io.timemates.backend.rsocket.authorization.types.UserAvatar
import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val avatar: UserAvatar?,
    val name: String?,
    val description: String?,
)