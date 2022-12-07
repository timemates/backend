package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.Code

@Serializable
sealed interface CreateInviteResult {
    @Serializable
    @SerialName("success")
    class Success(val code: Code) : CreateInviteResult

    @Serializable
    @SerialName("no_access")
    object NoAccess : CreateInviteResult
}