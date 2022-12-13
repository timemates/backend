package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.User

@Serializable
sealed interface GetUsersResult {
    @SerialName("success")
    @Serializable
    class Success(val list: List<User>) : GetUsersResult
}