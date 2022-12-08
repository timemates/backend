package org.tomadoro.backend.application.routes.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.EditUserResult
import org.tomadoro.backend.application.types.User
import org.tomadoro.backend.application.types.internal
import org.tomadoro.backend.usecases.users.EditUserUseCase

fun Route.editUser(editUserUseCase: EditUserUseCase) = patch<User.Patch> { patch ->
    authorized {
        when(editUserUseCase(it, patch.internal())) {
            is EditUserUseCase.Result.Success ->
                call.respond<EditUserResult>(EditUserResult.Success)
        }
    }
}