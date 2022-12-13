package org.tomadoro.backend.application.routes.timer.notes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetLatestUserNotesResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.PageToken
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.notes.GetLatestUserNotesUseCase

fun Route.getLastUserNotes(
    getLatestUserNotesUseCase: GetLatestUserNotesUseCase
) = get("getLastUserNotes") {
    val timerId = call.request.queryParameters.getOrFail("timer_id")
        .toInt()
        .let { TimersRepository.TimerId(it) }

    val pageToken = call.request.queryParameters.getOrFail("page_token")
        .let { PageToken(it) }

    val count = call.request.queryParameters.getOrFail("count")
        .toInt()
        .let { Count(it) }

    authorized { authorizedId ->
        val result = getLatestUserNotesUseCase(authorizedId, timerId, pageToken, count)

        call.respond(
            when (result) {
                is GetLatestUserNotesUseCase.Result.Success ->
                    GetLatestUserNotesResult.Success(
                        result.list.map(NotesRepository.Note::serializable)
                    )

                is GetLatestUserNotesUseCase.Result.NoAccess ->
                    GetLatestUserNotesResult.NoAccess

                is GetLatestUserNotesUseCase.Result.BadPageToken ->
                    return@get call.respond(HttpStatusCode.BadRequest, "Page token is invalid.")
            }
        )
    }
}