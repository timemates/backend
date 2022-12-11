package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.repositories.NotesRepository

@Serializable
@JvmInline
value class NoteMessage(val string: String)

fun NotesRepository.Message.serializable() = NoteMessage(string)