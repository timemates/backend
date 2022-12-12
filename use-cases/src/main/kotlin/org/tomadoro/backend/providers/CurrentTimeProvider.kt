package org.tomadoro.backend.providers

import org.tomadoro.backend.domain.value.DateTime

fun interface CurrentTimeProvider {
    fun provide(): DateTime
}