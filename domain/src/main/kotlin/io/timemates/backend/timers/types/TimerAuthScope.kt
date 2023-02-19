package io.timemates.backend.timers.types

import io.timemates.backend.features.authorization.Scope

sealed class TimerAuthScope : Scope {
    data object Write : TimerAuthScope()
    data object Read : TimerAuthScope()
}