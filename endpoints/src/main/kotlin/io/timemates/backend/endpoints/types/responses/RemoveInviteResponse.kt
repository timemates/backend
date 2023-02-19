package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RemoveInviteResponse {
    @Serializable
    @SerialName("success")
    object Success : RemoveInviteResponse

    @Serializable
    @SerialName("no_access")
    object NoAccess : RemoveInviteResponse

    @Serializable
    @SerialName("not_found")
    object NotFound : RemoveInviteResponse
}