package org.tomadoro.backend.application.routes.timer.notes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.AddNoteResult
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.notes.AddNoteUseCase

fun Route.addNote(addNoteUseCase: AddNoteUseCase) = post {
    val timerId = call.request.queryParameters.getOrFail("timer_id")
        .toInt()
        .let { TimersRepository.TimerId(it) }
    val message = call.request.queryParameters.getOrFail("message")
        .let { NotesRepository.Message(it) }
    authorized { authorizedId ->
        val result = addNoteUseCase(authorizedId, timerId, message)

        call.respond(when(result) {
            is AddNoteUseCase.Result.Success ->
                AddNoteResult.Success(result.noteId.serializable())
            is AddNoteUseCase.Result.NoAccess ->
                AddNoteResult.NoAccess
        })
    }
}