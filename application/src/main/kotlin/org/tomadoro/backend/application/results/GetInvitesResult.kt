package org.tomadoro.backend.application.results

import org.tomadoro.backend.application.types.Invite
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface GetInvitesResult {
    @Serializable
    @SerialName("success")
    class Success(val list: List<Invite>) : GetInvitesResult

    @Serializable
    @SerialName("no_access")
    object NoAccess : GetInvitesResult
}