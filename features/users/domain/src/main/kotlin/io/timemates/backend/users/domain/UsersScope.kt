package io.timemates.backend.users.domain

import io.timemates.backend.foundation.authorization.Scope

sealed class UsersScope : Scope {
    open class Read : UsersScope() {
        companion object : Read()
    }

    data object Write : Read()
}