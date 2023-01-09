package io.timemates.backend.types.value

import kotlin.time.Duration
import kotlin.time.DurationUnit

@JvmInline
value class UnixTime(val long: Long) {
    operator fun plus(long: Long) =
        UnixTime(this.long + long)

    operator fun minus(long: Long) =
        UnixTime(this.long - long)

    operator fun minus(unixTime: UnixTime): Milliseconds =
        Milliseconds(long - unixTime.long)

    operator fun plus(milliseconds: Milliseconds) = plus(milliseconds.long)
    operator fun minus(milliseconds: Milliseconds) = minus(milliseconds.long)

    operator fun plus(duration: Duration) = plus(duration.toLong(DurationUnit.MILLISECONDS))

    operator fun minus(duration: Duration) = minus(duration.toLong(DurationUnit.MILLISECONDS))
}