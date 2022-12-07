package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RemoveTokenResult {
    @Serializable
    @SerialName("success")
    object Success : RemoveTokenResult
}