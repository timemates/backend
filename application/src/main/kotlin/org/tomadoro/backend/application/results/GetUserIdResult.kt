package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.UserId

@Serializable
sealed interface GetUserIdResult {
    @Serializable
    @SerialName("success")
    class Success(@SerialName("user_id") val userId: UserId) : GetUserIdResult
}