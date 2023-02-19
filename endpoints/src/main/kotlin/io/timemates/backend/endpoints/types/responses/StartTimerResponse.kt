package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface StartTimerResponse {
    @Serializable
    @SerialName("success")
    object Success : StartTimerResponse

    @Serializable
    @SerialName("no_access")
    object NoAccess : StartTimerResponse
}