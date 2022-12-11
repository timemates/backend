package org.tomadoro.backend.application.types

import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.*
import org.tomadoro.backend.repositories.NotesRepository

@Serializable
class Note(
    val noteId: NoteId,
    val userId: UserId,
    val message: NoteMessage,
    val creationTime: Milliseconds
)

internal fun NotesRepository.Note.serializable(): Note {
    return Note(
        noteId.serializable(),
        userId.serializable(),
        message.serializable(),
        creationTime.serializable()
    )
}