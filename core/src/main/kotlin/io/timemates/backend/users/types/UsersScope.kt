package io.timemates.backend.users.types

import io.timemates.backend.features.authorization.Scope

sealed class UsersScope : Scope {
    open class Read : UsersScope() {
        companion object : Read()
    }

    data object Write : Read()
}