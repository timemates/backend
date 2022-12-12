package org.tomadoro.backend.application.routes.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.application.results.GetUsersResult
import org.tomadoro.backend.application.types.serializable
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.users.GetUsersUseCase

fun Route.getUsers(getUsersUseCase: GetUsersUseCase) = get("getUsersBatched") {
    val ids = call.request.queryParameters.getOrFail("ids")
        .split(",")
        .map { UsersRepository.UserId(it.toInt()) }

    when(val result = getUsersUseCase(ids)) {
        is GetUsersUseCase.Result.Success ->
            call.respond<GetUsersResult>(
                GetUsersResult.Success(result.collection.map { it.serializable() })
            )
    }
}