package io.timemates.backend.application.types.responses

import io.timemates.backend.application.types.Invite
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface GetInvitesResponse {
    @Serializable
    @SerialName("success")
    class Success(val list: List<Invite>) : GetInvitesResponse

    @Serializable
    @SerialName("no_access")
    object NoAccess : GetInvitesResponse
}