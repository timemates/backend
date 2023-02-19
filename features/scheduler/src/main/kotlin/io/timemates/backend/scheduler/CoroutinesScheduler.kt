package io.timemates.backend.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.time.Duration

public class CoroutinesScheduler(
    private val coroutineScope: CoroutineScope
) : Scheduler {
    /**
     * Map with all scheduled operations.
     */
    private val operations: ConcurrentMap<Any, Job> = ConcurrentHashMap()

    override fun <Key : Any> executeLaterWithDelay(key: Key, duration: Duration, operation: suspend () -> Unit) {
        if(operations[key] != null)
            throw IllegalStateException("Cannot schedule a operation, as there is already one.")

        operations[key] = coroutineScope.launch {
            delay(duration.inWholeMilliseconds)
            operation()
        }
    }

    override fun <Key : Any> cancel(key: Key) {
        operations[key]?.cancel()
    }
}