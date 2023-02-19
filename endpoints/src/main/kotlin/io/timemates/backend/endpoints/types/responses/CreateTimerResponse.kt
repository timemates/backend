package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.endpoints.types.value.TimerId

@Serializable
sealed interface CreateTimerResponse {
    @Serializable
    @SerialName("success")
    class Success(
        @SerialName("timer_id") val timerId: TimerId
    ) : CreateTimerResponse
}