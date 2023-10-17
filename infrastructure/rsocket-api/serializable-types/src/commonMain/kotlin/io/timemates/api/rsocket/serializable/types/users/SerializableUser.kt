package io.timemates.api.rsocket.serializable.types.users

import kotlinx.serialization.Serializable

@Serializable
data class SerializableUser(
    val id: Long,
    val name: String,
    val emailAddress: String?,
    val description: String?,
    val avatar: SerializableAvatar?,
)