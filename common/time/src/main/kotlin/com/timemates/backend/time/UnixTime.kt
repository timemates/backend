package com.timemates.backend.time

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName
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
        val result = long + duration.inWholeMilliseconds
        require(result >= 0) { "Unix time cannot be negative" }

        return UnixTime(result)
    }

    /**
     * Minuses current unix time and given [Duration].
     *
     * @return [UnixTime] - [Duration]
     */
    public operator fun minus(duration: Duration): UnixTime {
        val result = long - duration.inWholeMilliseconds
        require(result >= 0) { "Unix time cannot be negative" }

        return UnixTime(long - duration.inWholeMilliseconds)
    }

    /**
     * Returns value in milliseconds
     */
    public val inMilliseconds: Long
        get() = long

    public operator fun compareTo(other: UnixTime): Int = long.compareTo(other.long)

    public companion object : SafeConstructor<UnixTime, Long>() {
        override val displayName: String by wrapperTypeName()

        public val ZERO: UnixTime = UnixTime(0)
        public val INFINITE: UnixTime = UnixTime(Long.MAX_VALUE)

        /**
         * Instantiates the instance of [UnixTime].
         * Negative [value] is now allowed and will fail.
         */
        context(ValidationFailureHandler)
        override fun create(value: Long): UnixTime {
            return when {
                value < 0 -> onFail(FailureMessage.ofNegative())
                else -> UnixTime(long = value)
            }
        }
    }
}