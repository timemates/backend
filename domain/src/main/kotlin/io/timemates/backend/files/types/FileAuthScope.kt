package io.timemates.backend.files.types

import io.timemates.backend.features.authorization.Scope

sealed class FileAuthScope : Scope {
    data object Write : FileAuthScope()

    data object Read : FileAuthScope()
}