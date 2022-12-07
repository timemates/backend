package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.TimerId

@Serializable
sealed interface CreateTimerResult {
    @Serializable
    @SerialName("success")
    class Success(
        @SerialName("timer_id") val timerId: TimerId
    ) : CreateTimerResult
}