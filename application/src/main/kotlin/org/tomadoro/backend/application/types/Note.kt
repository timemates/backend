package org.tomadoro.backend.application.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.*
import org.tomadoro.backend.repositories.NotesRepository

@Serializable
class Note(
    @SerialName("note_id")
    val noteId: NoteId,
    @SerialName("user_id")
    val userId: UserId,
    val message: NoteMessage,
    @SerialName("is_viewed")
    val isViewed: Boolean,
    @SerialName("creation_time")
    val creationTime: Milliseconds
)

internal fun NotesRepository.Note.serializable(): Note {
    return Note(
        noteId.serializable(),
        userId.serializable(),
        message.serializable(),
        isViewed,
        creationTime.serializable()
    )
}