package org.tomadoro.backend.application.results

import org.tomadoro.backend.application.types.value.TimerId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface JoinByCodeResult {
    @Serializable
    @SerialName("success")
    class Success(
        @SerialName("timer_id") val timerId: TimerId
    ) : JoinByCodeResult

    @Serializable
    @SerialName("not_found")
    object NotFound : JoinByCodeResult
}