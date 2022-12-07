package org.tomadoro.backend.application.routes.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetTimersResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.usecases.timers.GetTimersUseCase

fun Route.getTimers(getTimers: GetTimersUseCase) {
    get("all") {
        authorized { userId ->
            val count = call.request.queryParameters.getOrFail("count").toInt()
            val offset = call.request.queryParameters.getOrFail("offset").toInt()
            val result: GetTimersResult =
                GetTimersResult.Success(
                    (getTimers(
                        userId,
                        offset..count + offset
                    ) as GetTimersUseCase.Result.Success).list.map { it.serializable() }
                )

            call.respond(result)
        }
    }
}