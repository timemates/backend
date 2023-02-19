package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.endpoints.types.value.InviteCode

@Serializable
sealed interface CreateInviteResponse {
    @Serializable
    @SerialName("success")
    class Success(val inviteCode: InviteCode) : CreateInviteResponse

    @Serializable
    @SerialName("no_access")
    object NoAccess : CreateInviteResponse
}