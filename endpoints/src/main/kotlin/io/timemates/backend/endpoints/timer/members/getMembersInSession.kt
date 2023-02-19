package io.timemates.backend.endpoints.timer.members

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.GetMembersInSessionResponse
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.PageToken
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.timers.members.GetMembersInSessionUseCase

fun Route.getMembersInSession(
    getMembersInSessionUseCase: GetMembersInSessionUseCase
) = get("members/getMembersInSession") {
    val pageToken = call.request.queryParameters["page_token"]
        ?.let { PageToken(it) }
    val count = call.request.queryParameters.getOrFail("count").toInt()
    val timerId = call.request.queryParameters.getOrFail("timer_id").toInt()

    authorized { authorized ->
        val result = getMembersInSessionUseCase(
            authorized.internal(), TimersRepository.TimerId(timerId), pageToken, Count(count)
        )

        call.respond(when(result) {
            is GetMembersInSessionUseCase.Result.Success ->
                GetMembersInSessionResponse.Success(
                    result.list.map { io.timemates.backend.endpoints.types.serializable() }
                )
            is GetMembersInSessionUseCase.Result.NoAccess ->
                GetMembersInSessionResponse.NoAccess
            is GetMembersInSessionUseCase.Result.BadPageToken ->
                return@get call.respond(HttpStatusCode.BadRequest, "Page token is invalid")
        })
    }
}