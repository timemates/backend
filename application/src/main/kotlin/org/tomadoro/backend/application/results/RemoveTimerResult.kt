package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RemoveTimerResult {
    @Serializable
    @SerialName("success")
    object Success : RemoveTimerResult

    @Serializable
    @SerialName("not_found")
    object NotFound : RemoveTimerResult
}