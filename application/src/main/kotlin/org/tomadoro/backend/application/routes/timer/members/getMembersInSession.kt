package org.tomadoro.backend.application.routes.timer.members

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetMembersInSessionResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.PageToken
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.members.GetMembersInSessionUseCase

fun Route.getMembersInSession(
    getMembersInSessionUseCase: GetMembersInSessionUseCase
) = get("members/getMembersInSession") {
    val pageToken = call.request.queryParameters.get("page_token")
        ?.let { PageToken(it) }
    val count = call.request.queryParameters.getOrFail("count").toInt()
    val timerId = call.request.queryParameters.getOrFail("timer_id").toInt()

    authorized { authorized ->
        val result = getMembersInSessionUseCase(
            authorized, TimersRepository.TimerId(timerId), pageToken, Count(count)
        )

        call.respond(when(result) {
            is GetMembersInSessionUseCase.Result.Success ->
                GetMembersInSessionResult.Success(
                    result.list.map(UsersRepository.User::serializable)
                )
            is GetMembersInSessionUseCase.Result.NoAccess ->
                GetMembersInSessionResult.NoAccess
            is GetMembersInSessionUseCase.Result.BadPageToken ->
                return@get call.respond(HttpStatusCode.BadRequest, "Page token is invalid")
        })
    }
}