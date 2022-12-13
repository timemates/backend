package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.NoteId

@Serializable
sealed interface AddNoteResult {
    @SerialName("success")
    @Serializable
    @JvmInline
    value class Success(@SerialName("note_id") val noteId: NoteId) : AddNoteResult

    /**
     * Marks that this feature isn't enabled for timer,
     * or user doesn't have access to timer, or it's restricted
     * to some group of people (reserved behaviour).
     */
    @SerialName("no_access")
    @Serializable
    object NoAccess : AddNoteResult
}