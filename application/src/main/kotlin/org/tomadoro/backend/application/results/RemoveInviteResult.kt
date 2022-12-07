package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RemoveInviteResult {
    @Serializable
    @SerialName("success")
    object Success : RemoveInviteResult

    @Serializable
    @SerialName("no_access")
    object NoAccess : RemoveInviteResult

    @Serializable
    @SerialName("not_found")
    object NotFound : RemoveInviteResult
}