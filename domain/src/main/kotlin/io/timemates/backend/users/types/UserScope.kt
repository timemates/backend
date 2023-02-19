package io.timemates.backend.users.types

import io.timemates.backend.features.authorization.Scope

sealed class UserScope : Scope {
    data object Read : UserScope()
    data object Write : UserScope()
}