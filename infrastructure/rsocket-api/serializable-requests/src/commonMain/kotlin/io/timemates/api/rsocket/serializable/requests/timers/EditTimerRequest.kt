package io.timemates.api.rsocket.serializable.requests.timers

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.timers.SerializableTimerSettingsPatch
import kotlinx.serialization.Serializable

@Serializable
data class EditTimerRequest(
    val timerId: Long,
    val name: String? = null,
    val description: String? = null,
    val settings: SerializableTimerSettingsPatch? = null,
) : RSocketRequest<EditTimerRequest.Result> {
    companion object Key : RSocketRequest.Key<EditTimerRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}