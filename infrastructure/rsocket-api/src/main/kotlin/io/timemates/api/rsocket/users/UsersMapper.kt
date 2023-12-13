package io.timemates.api.rsocket.users

import io.timemates.backend.users.types.Avatar
import io.timemates.backend.users.types.User
import io.timemates.api.users.types.User as RSUser

internal fun User.rs(): RSUser {
    return RSUser.create {
        id = this@rs.id.long
        name = this@rs.name.string
        this@rs.description?.string?.let { description = it }
        this@rs.emailAddress?.let { email = it.string }
        this@rs.avatar?.let { gravatarId = (it as Avatar.GravatarId).string }
    }
}