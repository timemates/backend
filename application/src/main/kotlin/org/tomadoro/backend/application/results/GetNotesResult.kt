package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.Note

@Serializable
sealed interface GetNotesResult {
    @SerialName("success")
    @Serializable
    @JvmInline
    value class Success(val list: List<Note>) : GetNotesResult

    /**
     * Marks that this feature isn't enabled for timer,
     * or user doesn't have access to timer, or it's restricted
     * to some group of people (reserved behaviour).
     */
    @SerialName("no_access")
    @Serializable
    object NoAccess : GetNotesResult
}