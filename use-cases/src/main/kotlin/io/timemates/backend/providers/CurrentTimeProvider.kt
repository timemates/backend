package io.timemates.backend.providers

import io.timemates.backend.types.value.UnixTime

fun interface CurrentTimeProvider {
    fun provide(): UnixTime
}