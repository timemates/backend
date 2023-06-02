package io.timemates.backend.files.types

import io.timemates.backend.features.authorization.Scope

sealed class FilesScope : Scope {
    data object Write : Read()

    open class Read : FilesScope() {
        companion object : Read()
    }
}