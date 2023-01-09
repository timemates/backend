package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface StopTimerResponse {
    @Serializable
    @SerialName("success")
    object Success : StopTimerResponse

    @Serializable
    @SerialName("no_access")
    object NoAccess : StopTimerResponse
}