package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.endpoints.types.User

@Serializable
sealed interface GetMembersInSessionResponse {
    @Serializable
    @SerialName("success")
    @JvmInline
    value class Success(val list: List<User>) : GetMembersInSessionResponse
    @Serializable
    @SerialName("no_access")
    object NoAccess : GetMembersInSessionResponse
}