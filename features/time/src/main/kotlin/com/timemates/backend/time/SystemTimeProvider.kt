package com.timemates.backend.time

import com.timemates.backend.validation.createOrThrow
import java.util.*

public class SystemTimeProvider(
    private val timeZone: TimeZone = TimeZone.getDefault()
) : TimeProvider {
    override fun provide(): UnixTime {
        return UnixTime.createOrThrow(Calendar.getInstance(timeZone).timeInMillis)
    }

}