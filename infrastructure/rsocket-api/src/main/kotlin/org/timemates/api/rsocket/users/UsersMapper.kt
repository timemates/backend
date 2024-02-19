package org.timemates.api.rsocket.users

import org.timemates.backend.types.users.Avatar
import org.timemates.backend.types.users.User
import org.timemates.api.users.types.User as RSUser

internal fun User.rs(): RSUser {
    return RSUser {
        id = this@rs.id.long
        name = this@rs.name.string
        this@rs.description?.string?.let { description = it }
        this@rs.emailAddress?.let { email = it.string }
        this@rs.avatar?.let { gravatarId = (it as Avatar.GravatarId).string }
    }
}