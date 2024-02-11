package io.timemates.backend.types.timers

import io.timemates.backend.foundation.authorization.Scope

sealed class TimersScope : Scope {
    data object Write : Read()
    open class Read : TimersScope() {
        companion object : Read()
    }
}