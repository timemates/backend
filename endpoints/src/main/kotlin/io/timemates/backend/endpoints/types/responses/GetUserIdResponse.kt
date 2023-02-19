package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.value.UserId

@Serializable
sealed interface GetUserIdResponse {
    @Serializable
    @SerialName("success")
    class Success(@SerialName("user_id") val userId: UserId) : GetUserIdResponse
}