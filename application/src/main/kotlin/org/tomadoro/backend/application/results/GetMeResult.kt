package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.User

@Serializable
sealed interface GetMeResult {
    @Serializable
    @SerialName("success")
    class Success(val user: User) : GetMeResult
}