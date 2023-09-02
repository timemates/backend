package com.timemates.backend.time

import com.timemates.backend.validation.createOrThrowInternally
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

public class SystemTimeProvider(
    private val timeZone: ZoneId = ZoneId.systemDefault(),
) : TimeProvider {
    private val clock = Clock.system(timeZone)

    override fun provide(): UnixTime {
        val instant = Instant.now(clock)
        return UnixTime.createOrThrowInternally(instant.toEpochMilli())
    }
}