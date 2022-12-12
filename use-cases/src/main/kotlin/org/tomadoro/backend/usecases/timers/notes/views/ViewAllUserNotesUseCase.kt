package org.tomadoro.backend.usecases.timers.notes.views

import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository

class ViewAllUserNotesUseCase(
    private val notesRepository: NotesRepository,
    private val timersRepository: TimersRepository
) {
    suspend operator fun invoke(
        authorizedId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        userToView: UsersRepository.UserId
    ): Result {
        if(!timersRepository.isMemberOf(authorizedId, timerId))
            return Result.NoAccess

        notesRepository.markViewed(userToView, timerId)
        return Result.Success
    }

    sealed interface Result {
        object Success : Result
        object NoAccess : Result
    }
}