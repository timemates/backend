package io.timemates.backend.application.types.responses

import io.timemates.backend.application.types.value.TimerId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface JoinByCodeResponse {
    @Serializable
    @SerialName("success")
    class Success(
        @SerialName("timer_id") val timerId: TimerId
    ) : JoinByCodeResponse

    @Serializable
    @SerialName("not_found")
    object NotFound : JoinByCodeResponse
}