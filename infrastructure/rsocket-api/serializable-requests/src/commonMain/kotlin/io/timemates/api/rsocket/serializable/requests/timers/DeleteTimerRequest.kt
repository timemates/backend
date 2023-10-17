package io.timemates.api.rsocket.serializable.requests.timers

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class DeleteTimerRequest(
    val timerId: Long,
) : RSocketRequest<DeleteTimerRequest.Result> {
    companion object Key : RSocketRequest.Key<DeleteTimerRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}