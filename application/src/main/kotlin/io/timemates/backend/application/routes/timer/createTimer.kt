package io.timemates.backend.application.routes.timer

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.application.types.responses.CreateTimerResponse
import io.timemates.backend.application.types.TimerSettings
import io.timemates.backend.application.types.internal
import io.timemates.backend.application.types.value.internal
import io.timemates.backend.application.types.value.serializable
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.usecases.timers.CreateTimerUseCase

@Serializable
data class CreateTimerRequest(
    val name: String,
    val settings: TimerSettings,
)

fun Route.createTimer(createTimer: CreateTimerUseCase) {
    post<CreateTimerRequest> { data ->
        authorized { userId ->
            val result: CreateTimerResponse =
                when (val result = createTimer(
                    userId.internal(), data.settings.internal(), TimerName(data.name)
                )) {
                    is CreateTimerUseCase.Result.Success ->
                        CreateTimerResponse.Success(result.timerId.serializable())
                    is CreateTimerUseCase.Result.TooManyCreations ->
                        return@post call.respond(HttpStatusCode.TooManyRequests)
                }

            call.respond(result)
        }
    }
}