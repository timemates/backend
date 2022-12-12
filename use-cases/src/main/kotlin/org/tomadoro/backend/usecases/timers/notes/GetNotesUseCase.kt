package org.tomadoro.backend.usecases.timers.notes

import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class GetNotesUseCase(
    private val notesRepository: NotesRepository,
    private val timersRepository: TimersRepository
) {
    /**
     * Gets notes of the specified [timerId].
     * @param ofUser optional parameter which makes use case
     * load only notes of specified user.
     * @param [afterNoteId] loads timers after note with specified id.
     */
    suspend operator fun invoke(
        authorizedId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        ofUser: UsersRepository.UserId?,
        afterNoteId: NotesRepository.NoteId?,
        count: Count
    ): Result {
        if (!timersRepository.isMemberOf(authorizedId, timerId)
            && timersRepository.getTimerSettings(timerId)?.isNotesEnabled != true
        )
            return Result.NoAccess

        return Result.Success(
            notesRepository.getNotes(
                timerId,
                authorizedId,
                afterNoteId ?: NotesRepository.NoteId(0), ofUser, count
            )
        )
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<NotesRepository.Note>) : Result
        /**
         * Marks that user didn't have access to timer
         * or this feature simply disabled.
         */
        object NoAccess : Result
    }
}