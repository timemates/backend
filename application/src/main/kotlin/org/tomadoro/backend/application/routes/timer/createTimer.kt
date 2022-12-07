package org.tomadoro.backend.application.routes.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.CreateTimerResult
import org.tomadoro.backend.application.types.TimerSettings
import org.tomadoro.backend.application.types.internal
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.usecases.timers.CreateTimerUseCase

@Serializable
data class CreateTimerRequest(
    val name: String,
    val settings: TimerSettings,
)

fun Route.createTimer(createTimer: CreateTimerUseCase) {
    post<CreateTimerRequest> { data ->
        authorized { userId ->
            val result: CreateTimerResult =
                when (val result = createTimer(
                    userId, data.settings.internal(), TimerName(data.name)
                )) {
                    is CreateTimerUseCase.Result.Success ->
                        CreateTimerResult.Success(result.timerId.serializable())
                }

            call.respond(result)
        }
    }
}