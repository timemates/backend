package io.timemates.backend.endpoints.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.GetTimersResponse
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.PageToken
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.timers.GetTimersUseCase

fun Route.getTimers(getTimers: GetTimersUseCase) {
    get("getTimers") {
        authorized { userId ->
            val count = call.request.queryParameters.getOrFail("count").toInt()
            val pageToken = call.request.queryParameters["from_id"]
            val result: GetTimersResponse =
                GetTimersResponse.Success(
                    (getTimers(
                        userId.internal(),
                        pageToken?.let { PageToken(it) },
                        Count(count)
                    ) as GetTimersUseCase.Result.Success).list.map { io.timemates.backend.endpoints.types.serializable() }
                )

            call.respond(result)
        }
    }
}