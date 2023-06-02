package io.timemates.backend.timers.types

import io.timemates.backend.features.authorization.Scope

sealed class TimersScope : Scope {
    data object Write : Read()
    open class Read : TimersScope() {
        companion object : Read()
    }
}