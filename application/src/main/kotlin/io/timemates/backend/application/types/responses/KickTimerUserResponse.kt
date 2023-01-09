package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface KickTimerUserResponse {
    @SerialName("success")
    @Serializable
    object Success : KickTimerUserResponse

    @SerialName("success")
    @Serializable
    object NoAccess : KickTimerUserResponse
}