package org.tomadoro.backend.application.routes.timer.notes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetNotesResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.notes.GetNotesUseCase

fun Route.getNotes(getNotesUseCase: GetNotesUseCase) = get("all") {
    val timerId = call.request.queryParameters.getOrFail("timer_id")
        .toInt()
        .let { TimersRepository.TimerId(it) }
    val ofUser = call.request.queryParameters["of_user"]
        ?.toInt()
        ?.let { UsersRepository.UserId(it) }
    val afterNoteId = call.request.queryParameters["after_note_id"]
        ?.toLong()
        ?.let { NotesRepository.NoteId(it) }
    val count = call.request.queryParameters.getOrFail("count")
        .toInt()
        .let { Count(it) }

    authorized { authorizedId ->
        val result = getNotesUseCase(authorizedId, timerId, ofUser, afterNoteId, count)

        call.respond(
            when (result) {
                is GetNotesUseCase.Result.Success -> GetNotesResult.Success(
                    result.list.map(NotesRepository.Note::serializable)
                )
                is GetNotesUseCase.Result.NoAccess -> GetNotesResult.NoAccess
            }
        )
    }
}