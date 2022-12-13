package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.DateTime

interface NotesRepository {
    suspend fun create(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId,
        message: Message,
        creationTime: DateTime
    ): NoteId

    suspend fun getNotes(
        timerId: TimersRepository.TimerId,
        byUser: UsersRepository.UserId,
        afterNoteId: NoteId,
        ofUser: UsersRepository.UserId?,
        count: Count
    ): List<Note>

    /**
     * Gets last posted notes of all users that have posted something.
     */
    suspend fun getLatestPostedNotes(
        timerId: TimersRepository.TimerId,
        byUser: UsersRepository.UserId,
        beforeNoteId: NoteId,
        count: Count
    ): List<Note>

    /**
     * Marks all notes as viewed by [byUser].
     */
    suspend fun markViewed(
        byUser: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    )

    data class Note(
        val noteId: NoteId,
        val userId: UsersRepository.UserId,
        val message: Message,
        val isViewed: Boolean,
        val creationTime: DateTime
    )

    @JvmInline
    value class NoteId(val long: Long)

    @JvmInline
    value class Message(val string: String) {
        init {
            require(string.length < 150) {
                "Message length should be less than 150"
            }
        }
    }
}