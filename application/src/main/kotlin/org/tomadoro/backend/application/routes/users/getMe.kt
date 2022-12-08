package org.tomadoro.backend.application.routes.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetMeResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.usecases.users.GetUsersUseCase

fun Route.getMe(getUsersUseCase: GetUsersUseCase) = get("me") {
    authorized {
        when(val result = getUsersUseCase(listOf(it))) {
            is GetUsersUseCase.Result.Success ->
                call.respond<GetMeResult>(GetMeResult.Success(result.collection.first().serializable()))
        }
    }
}