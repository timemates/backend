package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.Timer

@Serializable
sealed interface GetTimerResult {
    @Serializable
    @SerialName("success")
    class Success(val timer: Timer) : GetTimerResult

    @Serializable
    @SerialName("not_found")
    object NotFound : GetTimerResult
}