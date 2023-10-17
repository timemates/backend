package io.timemates.api.rsocket.serializable.requests.timers

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.timers.SerializableTimer
import kotlinx.serialization.Serializable

@Serializable
data class GetTimerRequest(
    val timerId: Long,
) : RSocketRequest<GetTimerRequest.Result> {
    companion object Key : RSocketRequest.Key<GetTimerRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data class Result(val timer: SerializableTimer)
}