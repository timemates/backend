package io.timemates.backend.serializable.types.users

import kotlinx.serialization.Serializable

@Serializable
data class SerializableUserPatch(
    val name: String? = null,
    val description: String? = null,
    val avatar: SerializableAvatar? = null,
)