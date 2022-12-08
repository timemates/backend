package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.User

@Serializable
sealed interface GetUsersResult {
    @Serializable
    class Success(val list: List<User>) : GetUsersResult
}