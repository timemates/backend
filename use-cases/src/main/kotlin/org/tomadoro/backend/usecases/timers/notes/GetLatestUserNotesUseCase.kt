package org.tomadoro.backend.usecases.timers.notes

import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.PageToken
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import java.util.*

class GetLatestUserNotesUseCase(
    private val timersRepository: TimersRepository,
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(
        authorizedId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId,
        pageToken: PageToken?,
        count: Count
    ): Result {
        if (!timersRepository.isMemberOf(authorizedId, timerId)
            && timersRepository.getTimerSettings(timerId)?.isNotesEnabled != true
        )
            return Result.NoAccess

        val lastId = if (pageToken == null)
            null else
            String(
                Base64.getDecoder().decode(pageToken.string)
            ).toLongOrNull() ?: return Result.BadPageToken

        return Result.Success(
            notesRepository.getLatestPostedNotes(
                timerId,
                authorizedId,
                lastId?.let { NotesRepository.NoteId(it) }
                    ?: NotesRepository.NoteId(Long.MAX_VALUE),
                count
            )
        )
    }

    sealed interface Result {
        @JvmInline
        value class Success(val list: List<NotesRepository.Note>) : Result
        object NoAccess : Result
        object BadPageToken : Result
    }
}