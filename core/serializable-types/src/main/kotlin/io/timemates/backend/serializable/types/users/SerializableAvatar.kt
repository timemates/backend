package io.timemates.backend.serializable.types.users

import io.timemates.backend.users.types.Avatar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializableAvatar {
    @SerialName("gravatar")
    data class Gravatar(val gravatarId: String) : SerializableAvatar

    @SerialName("timemates")
    data class TimeMates(val fileId: String) : SerializableAvatar
}

fun Avatar.serializable(): SerializableAvatar {
    return when (this) {
        is Avatar.FileId -> SerializableAvatar.TimeMates(string)
        is Avatar.GravatarId -> SerializableAvatar.Gravatar(string)
    }
}