package io.timemates.backend.endpoints.types.responses

import io.timemates.backend.endpoints.types.value.Count
import io.timemates.backend.endpoints.types.value.Milliseconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface StartEmailAuthorizationResponse {
    @Serializable
    @SerialName("send_failed")
    object SendFailed : StartEmailAuthorizationResponse

    @Serializable
    @SerialName("success")
    class Success(
        @SerialName("expires_at")
        val expiresAt: Milliseconds,
        val attempts: Count
    ) : StartEmailAuthorizationResponse

    @Serializable
    @SerialName("attempts_exceed")
    object AttemptsExceed : StartEmailAuthorizationResponse
}