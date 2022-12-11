package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable

@Serializable
sealed interface KickTimerUserResult {
    @Serializable
    object Success : KickTimerUserResult

    @Serializable
    object NoAccess : KickTimerUserResult
}