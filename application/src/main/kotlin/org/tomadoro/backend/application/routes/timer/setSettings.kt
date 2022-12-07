package org.tomadoro.backend.application.routes.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.SetTimerSettingsResult
import org.tomadoro.backend.application.types.TimerSettings
import org.tomadoro.backend.application.types.internal
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.usecases.timers.SetTimerSettingsUseCase

fun Route.setSettings(setSettings: SetTimerSettingsUseCase) {
    patch<TimerSettings.Patch> { data ->
        authorized { userId ->
            val timerId = call.request.queryParameters.getOrFail("id").toInt()
            val result = setSettings(
                userId, TimersRepository.TimerId(timerId),
                data.internal()
            )

            val response = when (result) {
                is SetTimerSettingsUseCase.Result.Success ->
                    SetTimerSettingsResult.Success

                is SetTimerSettingsUseCase.Result.NoAccess ->
                    SetTimerSettingsResult.NoAccess
            }

            call.respond(response)
        }
    }
}