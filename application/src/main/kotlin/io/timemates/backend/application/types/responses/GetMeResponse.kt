package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.User

@Serializable
sealed interface GetMeResponse {
    @Serializable
    @SerialName("success")
    class Success(val user: User) : GetMeResponse
}