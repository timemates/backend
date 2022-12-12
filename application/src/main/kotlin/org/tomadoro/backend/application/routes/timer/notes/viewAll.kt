package org.tomadoro.backend.application.routes.timer.notes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.ViewAllUserNotesResult
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.notes.views.ViewAllUserNotesUseCase

fun Route.viewAllNotes(viewAllUserNotesUseCase: ViewAllUserNotesUseCase) =
    post("view/all") {
        val timerId = call.request.queryParameters.getOrFail("timer_id").toInt()
        val userId = call.request.queryParameters.getOrFail("user_id").toInt()
        authorized { authorizedId ->
            val result = viewAllUserNotesUseCase(
                authorizedId,
                TimersRepository.TimerId(timerId),
                UsersRepository.UserId(userId)
            )

            call.respond(when(result) {
                is ViewAllUserNotesUseCase.Result.Success ->
                    ViewAllUserNotesResult.Success
                is ViewAllUserNotesUseCase.Result.NoAccess ->
                    ViewAllUserNotesResult.NoAccess
            })
        }
    }