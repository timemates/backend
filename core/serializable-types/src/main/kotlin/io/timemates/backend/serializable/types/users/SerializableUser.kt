package io.timemates.backend.serializable.types.users

import io.timemates.backend.users.types.User
import kotlinx.serialization.Serializable

@Serializable
data class SerializableUser(
    val id: Long,
    val name: String,
    val emailAddress: String?,
    val description: String?,
    val avatar: SerializableAvatar?,
)

fun User.serializable(): SerializableUser {
    return SerializableUser(
        id.long,
        name.string,
        emailAddress?.string,
        description = description?.string,
        avatar = avatar?.serializable(),
    )
}