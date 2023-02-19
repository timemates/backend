package io.timemates.backend.endpoints.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.GetMeResponse
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.usecases.users.GetUsersUseCase

fun Route.getMe(getUsersUseCase: GetUsersUseCase) = get("getMe") {
    authorized {
        when(val result = getUsersUseCase(listOf(it.internal()))) {
            is GetUsersUseCase.Result.Success ->
                call.respond<GetMeResponse>(GetMeResponse.Success(io.timemates.backend.endpoints.types.serializable()))
        }
    }
}