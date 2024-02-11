package io.timemates.backend.authorization.types

import io.timemates.backend.features.authorization.Scope

sealed class AuthorizationsScope : Scope {
    open class Read : AuthorizationsScope() {
        companion object : Read()
    }

    data object Write : Read()
}