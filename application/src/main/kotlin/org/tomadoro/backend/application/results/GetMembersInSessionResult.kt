package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.User

@Serializable
sealed interface GetMembersInSessionResult {
    @Serializable
    @SerialName("success")
    @JvmInline
    value class Success(val list: List<User>) : GetMembersInSessionResult
    @Serializable
    @SerialName("no_access")
    object NoAccess : GetMembersInSessionResult
}