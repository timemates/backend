package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.DateTime

interface NotesRepository {
    suspend fun create(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId,
        message: Message,
        creationTime: DateTime
    ): NoteId

    suspend fun getNotes(
        timerId: TimersRepository.TimerId,
        afterNoteId: NoteId,
        ofUser: UsersRepository.UserId?,
        count: Count
    ): List<Note>

    class Note(
        val noteId: NoteId,
        val userId: UsersRepository.UserId,
        val message: Message,
        val creationTime: DateTime
    )

    @JvmInline
    value class NoteId(val long: Long)

    @JvmInline
    value class Message(val string: String) {
        init {
            require(string.length < 1000) {
                "Message length should be less than 1000"
            }
        }
    }
}