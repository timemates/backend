package org.tomadoro.backend.providers

import org.tomadoro.backend.domain.value.DateTime

object MockedCurrentTimeProvider : CurrentTimeProvider {
    override fun provide(): DateTime {
        return DateTime(System.currentTimeMillis())
    }
}