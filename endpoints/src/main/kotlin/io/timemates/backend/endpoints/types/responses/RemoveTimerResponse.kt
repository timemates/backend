package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RemoveTimerResponse {
    @Serializable
    @SerialName("success")
    object Success : RemoveTimerResponse

    @Serializable
    @SerialName("not_found")
    object NotFound : RemoveTimerResponse
}