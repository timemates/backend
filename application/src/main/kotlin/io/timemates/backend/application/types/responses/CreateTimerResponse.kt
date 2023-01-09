package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.value.TimerId

@Serializable
sealed interface CreateTimerResponse {
    @Serializable
    @SerialName("success")
    class Success(
        @SerialName("timer_id") val timerId: TimerId
    ) : CreateTimerResponse
}