package io.timemates.backend.providers

import io.timemates.backend.types.value.UnixTime
import java.util.*

class SystemCurrentTimeProvider(private val timeZone: TimeZone) : CurrentTimeProvider {
    override fun provide(): UnixTime {
        return UnixTime(Calendar.getInstance(timeZone).timeInMillis)
    }
}