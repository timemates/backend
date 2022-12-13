package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.Note

@Serializable
sealed interface GetLatestUserNotesResult {
    @SerialName("success")
    @Serializable
    class Success(val list: List<Note>) : GetLatestUserNotesResult

    @SerialName("no_access")
    @Serializable
    object NoAccess : GetLatestUserNotesResult
}