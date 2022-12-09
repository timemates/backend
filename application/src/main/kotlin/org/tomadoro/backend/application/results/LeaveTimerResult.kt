package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable

@Serializable
sealed interface LeaveTimerResult {
    @Serializable
    object Success : LeaveTimerResult
}