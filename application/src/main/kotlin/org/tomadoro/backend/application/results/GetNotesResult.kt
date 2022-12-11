package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.Note

@Serializable
sealed interface GetNotesResult {
    @Serializable
    @JvmInline
    value class Success(val list: List<Note>) : GetNotesResult

    /**
     * Marks that this feature isn't enabled for timer,
     * or user doesn't have access to timer, or it's restricted
     * to some group of people (reserved behaviour).
     */
    @Serializable
    object NoAccess : GetNotesResult
}