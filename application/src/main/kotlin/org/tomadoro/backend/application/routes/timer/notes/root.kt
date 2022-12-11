package org.tomadoro.backend.application.routes.timer.notes

import io.ktor.server.routing.*
import org.tomadoro.backend.usecases.timers.notes.AddNoteUseCase
import org.tomadoro.backend.usecases.timers.notes.GetNotesUseCase

fun Route.timerNotesRoot(
    addNoteUseCase: AddNoteUseCase,
    getNotesUseCase: GetNotesUseCase
) = route("notes") {
    addNote(addNoteUseCase)
    getNotes(getNotesUseCase)
}