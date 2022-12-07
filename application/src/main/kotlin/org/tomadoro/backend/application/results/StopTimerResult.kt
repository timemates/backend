package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface StopTimerResult {
    @Serializable
    @SerialName("success")
    object Success : StopTimerResult

    @Serializable
    @SerialName("no_access")
    object NoAccess : StopTimerResult
}