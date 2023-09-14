package io.timemates.backend.rsocket.features.users.requests

import io.timemates.backend.rsocket.features.users.types.User
import kotlinx.serialization.Serializable

@Serializable
data class EditUserRequest(
    val avatar: User.Avatar?,
    val name: String?,
    val description: String?,
)