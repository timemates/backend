package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.Timer

@Serializable
sealed interface GetTimersResponse {
    @Serializable
    @SerialName("success")
    class Success(val list: List<Timer>) : GetTimersResponse
}