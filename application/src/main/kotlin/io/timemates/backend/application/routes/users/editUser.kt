package io.timemates.backend.application.routes.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.application.types.responses.EditUserResponse
import io.timemates.backend.application.types.User
import io.timemates.backend.application.types.internal
import io.timemates.backend.application.types.value.internal
import io.timemates.backend.usecases.users.EditUserUseCase

fun Route.editUser(editUserUseCase: EditUserUseCase) = patch<User.Patch> { patch ->
    authorized {
        when(editUserUseCase(it.internal(), patch.internal())) {
            is EditUserUseCase.Result.Success ->
                call.respond<EditUserResponse>(EditUserResponse.Success)
        }
    }
}