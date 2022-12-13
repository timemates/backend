package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface EditUserResult {
    @SerialName("success")
    @Serializable
    object Success : EditUserResult
}