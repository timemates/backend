package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.endpoints.types.User

@Serializable
sealed interface GetUsersResponse {
    @SerialName("success")
    @Serializable
    class Success(val list: List<User>) : GetUsersResponse
}