package org.timemates.backend.types.auth

import org.timemates.backend.foundation.authorization.Scope

sealed class AuthorizationsScope : Scope {
    open class Read : AuthorizationsScope() {
        companion object : Read()
    }

    data object Write : Read()
}