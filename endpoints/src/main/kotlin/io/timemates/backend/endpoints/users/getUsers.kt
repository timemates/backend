package io.timemates.backend.endpoints.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.endpoints.types.responses.GetUsersResponse
import io.timemates.backend.endpoints.types.serializable
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.usecases.users.GetUsersUseCase

fun Route.getUsers(getUsersUseCase: GetUsersUseCase) = get("getUsersBatched") {
    val ids = call.request.queryParameters.getOrFail("ids")
        .split(",")
        .map { UsersRepository.UserId(it.toInt()) }

    when(val result = getUsersUseCase(ids)) {
        is GetUsersUseCase.Result.Success ->
            call.respond<GetUsersResponse>(
                GetUsersResponse.Success(result.collection.map { io.timemates.backend.endpoints.types.serializable() })
            )
    }
}