package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface LeaveTimerResponse {
    @SerialName("success")
    @Serializable
    object Success : LeaveTimerResponse
}