package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable

@Serializable
sealed interface ViewAllUserNotesResult {
    @Serializable
    object Success : ViewAllUserNotesResult
    @Serializable
    object NoAccess : ViewAllUserNotesResult
}