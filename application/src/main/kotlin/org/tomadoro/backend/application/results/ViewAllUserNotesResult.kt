package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ViewAllUserNotesResult {
    @SerialName("success")
    @Serializable
    object Success : ViewAllUserNotesResult
    @Serializable
    @SerialName("no_access")
    object NoAccess : ViewAllUserNotesResult
}