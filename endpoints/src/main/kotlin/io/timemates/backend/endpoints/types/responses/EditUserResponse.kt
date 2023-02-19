package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface EditUserResponse {
    @SerialName("success")
    @Serializable
    object Success : EditUserResponse
}