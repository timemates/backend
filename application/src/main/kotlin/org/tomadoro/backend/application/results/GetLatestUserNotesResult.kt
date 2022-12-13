package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.Note

@Serializable
sealed interface GetLatestUserNotesResult {
    @Serializable
    class Success(val list: List<Note>) : GetLatestUserNotesResult

    @Serializable
    object NoAccess : GetLatestUserNotesResult
}