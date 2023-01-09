package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RemoveTokenResponse {
    @Serializable
    @SerialName("success")
    object Success : RemoveTokenResponse
}