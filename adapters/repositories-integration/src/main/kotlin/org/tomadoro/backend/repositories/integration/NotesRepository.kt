package org.tomadoro.backend.repositories.integration

import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.datasource.DbTimerNotesDatasource
import org.tomadoro.backend.repositories.NotesRepository as NotesRepositoryContract

class NotesRepository(
    private val dbTimerNotesDatasource: DbTimerNotesDatasource
) : NotesRepositoryContract {
    override suspend fun create(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId,
        message: NotesRepository.Message,
        creationTime: DateTime
    ): NotesRepository.NoteId {
        return NotesRepository.NoteId(
            dbTimerNotesDatasource.createNote(
                timerId.int, userId.int, message.string, creationTime.long
            )
        )
    }

    override suspend fun getNotes(
        timerId: TimersRepository.TimerId,
        afterNoteId: NotesRepository.NoteId,
        ofUser: UsersRepository.UserId?,
        count: Count
    ): List<NotesRepository.Note> {
        return dbTimerNotesDatasource.getNotes(
            timerId.int, ofUser?.int, afterNoteId.long, count.int
        ).map { it.toExternal() }
    }

    private fun DbTimerNotesDatasource.Note.toExternal(): NotesRepository.Note {
        return NotesRepository.Note(
            NotesRepository.NoteId(noteId),
            UsersRepository.UserId(userId),
            NotesRepository.Message(message),
            DateTime(time)
        )
    }
}