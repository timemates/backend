package io.timemates.backend.endpoints.timer

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.SetTimerSettingsResponse
import io.timemates.backend.endpoints.types.TimerSettings
import io.timemates.backend.endpoints.types.internal
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.timers.SetTimerSettingsUseCase

fun Route.setSettings(setSettings: SetTimerSettingsUseCase) {
    patch<TimerSettings.Patch> { data ->
        authorized { userId ->
            val timerId = call.request.queryParameters.getOrFail("id").toInt()
            val result = setSettings(
                userId.internal(), TimersRepository.TimerId(timerId),
                data.internal()
            )

            val response = when (result) {
                is SetTimerSettingsUseCase.Result.Success ->
                    SetTimerSettingsResponse.Success

                is SetTimerSettingsUseCase.Result.NoAccess ->
                    SetTimerSettingsResponse.NoAccess
            }

            call.respond(response)
        }
    }
}