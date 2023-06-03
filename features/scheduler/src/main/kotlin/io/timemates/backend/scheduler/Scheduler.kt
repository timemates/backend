package io.timemates.backend.scheduler

import kotlin.time.Duration

public interface Scheduler {
    /**
     * Executes given operation with delay.
     *
     * @param key identifier of the [operation]
     * @param duration time to wait
     * @param operation to be invoked after delay
     */
    public fun <Key : Any> withDelay(
        key: Key,
        duration: Duration,
        operation: suspend () -> Unit,
    )

    /**
     * Cancels operation with given [key]
     */
    public fun <Key : Any> cancel(key: Key)
}