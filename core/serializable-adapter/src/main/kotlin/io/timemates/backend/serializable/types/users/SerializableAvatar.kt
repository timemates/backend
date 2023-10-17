package io.timemates.backend.serializable.types.users

import io.timemates.api.rsocket.serializable.types.users.SerializableAvatar
import io.timemates.backend.users.types.Avatar

fun Avatar.serializable(): SerializableAvatar {
    return when (this) {
        is Avatar.FileId -> SerializableAvatar.TimeMates(string)
        is Avatar.GravatarId -> SerializableAvatar.Gravatar(string)
    }
}