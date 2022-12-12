package org.tomadoro.backend.application.routes.timer.members

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetMembersResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.domain.value.Count
import org.tomadoro.backend.domain.value.PageToken
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.members.GetMembersUseCase

fun Route.getMembers(getMembersUseCase: GetMembersUseCase) =
    get("members/getMembers") {
        val pageToken = call.request.queryParameters.get("page_token")
            ?.let { PageToken(it) }
        val count = call.request.queryParameters.getOrFail("count").toInt()
        val timerId = call.request.queryParameters.getOrFail("timer_id").toInt()

        authorized { authorized ->
            val result = getMembersUseCase(
                authorized,
                TimersRepository.TimerId(timerId),
                pageToken,
                Count(count)
            )

            call.respond(when(result) {
                is GetMembersUseCase.Result.Success ->
                    GetMembersResult.Success(
                        result.list.map(UsersRepository.User::serializable),
                        result.nextPageToken.serializable()
                    )
                is GetMembersUseCase.Result.NoAccess ->
                    GetMembersResult.NoAccess
                is GetMembersUseCase.Result.BadPageToken ->
                    GetMembersResult.BadPageToken
            })
        }
    }