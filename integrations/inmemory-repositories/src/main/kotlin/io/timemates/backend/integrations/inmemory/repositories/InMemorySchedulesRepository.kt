package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.SchedulesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import io.timemates.backend.repositories.SchedulesRepository as Contract

class InMemorySchedulesRepository(private val scope: CoroutineScope) : Contract {
    private val singles = ConcurrentHashMap<Any, Job>()
    private val schedules = ConcurrentHashMap<Any, Job>()
    private val singlesMutex = Mutex()

    override suspend fun <K : Any> schedule(
        key: K, waitMs: Duration, job: suspend () -> Unit
    ) {
        cancel(key)
        singles[key] = scope.launch {
            delay(waitMs.inWholeMilliseconds)
            job()
        }
    }

    override suspend fun <K : Any> single(
        key: K, action: suspend SchedulesRepository.() -> Unit
    ) = singlesMutex.withLock {
        if (singles[key] == null)
            singles[key] = scope.launch {
                action()
            }
    }

    override suspend fun <K : Any> unbindSingle(key: K): Unit =
        singlesMutex.withLock {
            singles[key]?.cancel()
            singles.remove(key)
        }

    override suspend fun <K : Any> cancel(key: K) {
        schedules[key]?.cancel()
        schedules.remove(key)
    }

}