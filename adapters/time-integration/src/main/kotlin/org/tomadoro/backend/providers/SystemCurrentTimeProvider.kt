package org.tomadoro.backend.providers

import org.tomadoro.backend.domain.DateTime
import java.util.*

class SystemCurrentTimeProvider(private val timeZone: TimeZone) : CurrentTimeProvider {
    override fun provide(): DateTime {
        return DateTime(Calendar.getInstance(timeZone).timeInMillis)
    }
}