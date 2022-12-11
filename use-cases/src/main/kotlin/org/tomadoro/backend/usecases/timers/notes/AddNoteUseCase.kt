package org.tomadoro.backend.usecases.timers.notes

import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class AddNoteUseCase(
    private val notesRepository: NotesRepository,
    private val timersRepository: TimersRepository,
    private val timeProvider: CurrentTimeProvider
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        message: NotesRepository.Message
    ): Result {
        if (!timersRepository.isMemberOf(userId, timerId)
            || timersRepository.getTimer(timerId)?.settings?.isNotesEnabled != true
        ) {
            return Result.NoAccess
        }

        return Result.Success(
            notesRepository.create(timerId, userId, message, timeProvider.provide())
        )
    }

    sealed interface Result {
        @JvmInline
        value class Success(val noteId: NotesRepository.NoteId) : Result

        /**
         * Marks that this feature isn't enabled for timer,
         * or user doesn't have access to timer, or it's restricted
         * to some group of people (reserved behaviour).
         */
        object NoAccess : Result
    }
}