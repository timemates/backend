package io.timemates.backend.rsocket.users.types

import io.timemates.backend.users.types.Avatar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val emailAddress: String?,
    val description: String?,
    val avatar: Avatar?,
) {

    @Serializable
    data class Patch(
        val name: String? = null,
        val description: String? = null,
        val avatar: Avatar? = null,
    )

    @Serializable
    sealed interface Avatar {
        @SerialName("gravatar")
        data class Gravatar(val gravatarId: String) : Avatar

        @SerialName("timemates")
        data class TimeMates(val fileId: String) : Avatar
    }
}