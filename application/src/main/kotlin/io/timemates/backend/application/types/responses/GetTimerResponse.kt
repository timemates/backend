package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.Timer

@Serializable
sealed interface GetTimerResponse {
    @Serializable
    @SerialName("success")
    class Success(val timer: Timer) : GetTimerResponse

    @Serializable
    @SerialName("not_found")
    object NotFound : GetTimerResponse
}