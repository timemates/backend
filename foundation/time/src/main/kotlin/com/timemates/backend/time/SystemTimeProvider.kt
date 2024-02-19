package com.timemates.backend.time

import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

public class SystemTimeProvider(
    timeZone: ZoneId = ZoneId.systemDefault(),
) : TimeProvider {
    private val clock = Clock.system(timeZone)

    @OptIn(ValidationDelicateApi::class)
    override fun provide(): UnixTime {
        val instant = Instant.now(clock)
        return UnixTime.createUnsafe(instant.toEpochMilli())
    }
}