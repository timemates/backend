package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface SetTimerSettingsResponse {
    @Serializable
    @SerialName("success")
    object Success : SetTimerSettingsResponse

    @Serializable
    @SerialName("no_access")
    object NoAccess : SetTimerSettingsResponse
}