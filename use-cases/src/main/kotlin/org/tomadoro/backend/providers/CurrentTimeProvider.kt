package org.tomadoro.backend.providers

import org.tomadoro.backend.domain.DateTime

fun interface CurrentTimeProvider {
    fun provide(): DateTime
}