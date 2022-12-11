package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.repositories.NotesRepository

@Serializable
@JvmInline
value class NoteId(val long: Long)

fun NotesRepository.NoteId.serializable() = NoteId(long)