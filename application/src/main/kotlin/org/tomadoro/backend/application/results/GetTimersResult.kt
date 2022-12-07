package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.Timer

@Serializable
sealed interface GetTimersResult {
    @Serializable
    @SerialName("success")
    class Success(val list: List<Timer>) : GetTimersResult
}