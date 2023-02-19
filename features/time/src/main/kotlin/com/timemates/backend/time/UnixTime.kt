package com.timemates.backend.time

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope
import com.timemates.backend.validation.createOrThrow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * The class that represents unix-time.
 *
 * @see TimeProvider
 */
@JvmInline
public value class UnixTime private constructor(private val long: Long) {
    /**
     * Minuses current unix time from another.
     * @return [Duration] between given times.
     */
    public operator fun minus(other: UnixTime): Duration {
        return (long - other.long).milliseconds
    }

    /**
     * Pluses current unix time and given [Duration].
     *
     * @return [UnixTime] + [Duration]
     */
    public operator fun plus(duration: Duration): UnixTime {
        return createOrThrow(long + duration.inWholeMilliseconds)
    }

    /**
     * Minuses current unix time and given [Duration].
     *
     * @return [UnixTime] - [Duration]
     */
    public operator fun minus(duration: Duration): UnixTime {
        return createOrThrow(long - duration.inWholeMilliseconds)
    }

    /**
     * Returns value in milliseconds
     */
    public val inMilliseconds: Long
        get() = long

    public companion object : SafeConstructor<UnixTime, Long>() {
        public val ZERO: UnixTime = UnixTime(0)
        /**
         * Instantiates the instance of [UnixTime].
         * Negative [value] is now allowed and will fail.
         */
        context(ValidationScope)
        override fun create(value: Long): UnixTime {
            return when {
                value < 0 -> fail(NEGATIVE_IS_NOT_ALLOWED_MESSAGE)
                else -> UnixTime(long = value)
            }
        }

        private val NEGATIVE_IS_NOT_ALLOWED_MESSAGE = ReadableMessage("Negative Unix-time is not allowed.")
    }
}