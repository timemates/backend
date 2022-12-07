package org.tomadoro.backend.providers

import org.tomadoro.backend.domain.DateTime

object MockedCurrentTimeProvider : CurrentTimeProvider {
    override fun provide(): DateTime {
        return DateTime(System.currentTimeMillis())
    }
}