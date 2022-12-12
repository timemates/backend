package org.tomadoro.backend.repositories.integration

import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.datasource.DbTimerNotesDatasource
import org.tomadoro.backend.repositories.integration.datasource.DbTimerNotesViewsDataSource
import org.tomadoro.backend.repositories.NotesRepository as NotesRepositoryContract

class NotesRepository(
    private val dbTimerNotesDatasource: DbTimerNotesDatasource,
    private val dbTimerNotesViewsDataSource: DbTimerNotesViewsDataSource
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
        byUser: UsersRepository.UserId,
        afterNoteId: NotesRepository.NoteId,
        ofUser: UsersRepository.UserId?,
        count: Count
    ): List<NotesRepository.Note> {
        val notes = dbTimerNotesDatasource.getNotes(
            timerId.int, ofUser?.int, afterNoteId.long, count.int
        )

        // fast-path: if getting user is user itself,
        // it means that he already viewed, because he sent these notes
        return if(byUser == ofUser) {
            notes.map { it.toExternal(true) }
        } else {
            val viewed = dbTimerNotesViewsDataSource.filterViewed(
                byUser.int, notes.map(DbTimerNotesDatasource.Note::noteId)
            ).also { println(it) }

            notes.map { it.toExternal(viewed.any { id -> it.noteId == id }) }
        }
    }

    override suspend fun markViewed(byUser: UsersRepository.UserId, timerId: TimersRepository.TimerId) {
        dbTimerNotesDatasource.setAllNotesViewed(byUser.int, timerId.int)
    }

    private fun DbTimerNotesDatasource.Note.toExternal(isViewed: Boolean): NotesRepository.Note {
        return NotesRepository.Note(
            NotesRepository.NoteId(noteId),
            UsersRepository.UserId(userId),
            NotesRepository.Message(message),
            isViewed,
            DateTime(time)
        )
    }
}