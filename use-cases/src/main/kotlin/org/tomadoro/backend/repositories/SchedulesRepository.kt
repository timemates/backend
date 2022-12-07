package org.tomadoro.backend.repositories

import kotlin.time.Duration

interface SchedulesRepository {
    suspend fun <K : Any> schedule(
        key: K,
        waitMs: Duration,
        job: suspend () -> Unit
    )

    /**
     * Sets single scheduling operation for [key].
     * If there is already an action – [action] is not executed.
     */
    suspend fun <K : Any> single(
        key: K,
        action: suspend SchedulesRepository.() -> Unit
    )

    suspend fun <K : Any> unbindSingle(key: K)

    suspend fun <K : Any> cancel(key: K)
}

suspend fun <K : Any> SchedulesRepository.cancelAndSchedule(
    key: K,
    waitMs: Duration,
    job: suspend () -> Unit
) {
    cancel(key)
    schedule(key, waitMs, job)
}