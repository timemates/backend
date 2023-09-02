package io.timemates.backend.rsocket.authorization.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class UserAvatar {
    @SerialName("timemates")
    data class TimeMates(val fileId: String) : UserAvatar()

    @SerialName("gravatar")
    data class Gravatar(val identifier: String) : UserAvatar()
}