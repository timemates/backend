package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface KickTimerUserResult {
    @SerialName("success")
    @Serializable
    object Success : KickTimerUserResult

    @SerialName("success")
    @Serializable
    object NoAccess : KickTimerUserResult
}