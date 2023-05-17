package io.timemates.backend.authorization.types

import io.timemates.backend.features.authorization.Scope

sealed class AuthorizationScope : Scope {
    open class Read : AuthorizationScope() {
        companion object : Read()
    }

    data object Write : Read()
}