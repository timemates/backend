package org.timemates.backend.types.users

import org.timemates.backend.foundation.authorization.Scope

sealed class UsersScope : Scope {
    open class Read : UsersScope() {
        companion object : Read()
    }

    data object Write : Read()
}