package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.User

@Serializable
sealed interface GetUsersResponse {
    @SerialName("success")
    @Serializable
    class Success(val list: List<User>) : GetUsersResponse
}