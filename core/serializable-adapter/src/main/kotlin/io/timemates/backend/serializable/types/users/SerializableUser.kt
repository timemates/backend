package io.timemates.backend.serializable.types.users

import io.timemates.api.rsocket.serializable.types.users.SerializableUser
import io.timemates.backend.users.types.User

fun User.serializable(): SerializableUser {
    return SerializableUser(
        id.long,
        name.string,
        emailAddress?.string,
        description = description?.string,
        avatar = avatar?.serializable(),
    )
}