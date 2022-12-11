package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.NoteId

@Serializable
sealed interface AddNoteResult {
    @Serializable
    @JvmInline
    value class Success(val noteId: NoteId) : AddNoteResult

    /**
     * Marks that this feature isn't enabled for timer,
     * or user doesn't have access to timer, or it's restricted
     * to some group of people (reserved behaviour).
     */
    @Serializable
    object NoAccess : AddNoteResult
}