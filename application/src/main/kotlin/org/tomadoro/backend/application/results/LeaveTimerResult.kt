package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface LeaveTimerResult {
    @SerialName("success")
    @Serializable
    object Success : LeaveTimerResult
}