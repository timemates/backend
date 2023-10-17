package io.timemates.api.rsocket.serializable.types.users

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializableAvatar {
    @SerialName("gravatar")
    data class Gravatar(val gravatarId: String) : SerializableAvatar

    @SerialName("timemates")
    data class TimeMates(val fileId: String) : SerializableAvatar
}