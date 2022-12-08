package org.tomadoro.backend.application.routes.users

import io.ktor.server.routing.*
import org.tomadoro.backend.usecases.users.EditUserUseCase
import org.tomadoro.backend.usecases.users.GetUsersUseCase
import org.tomadoro.backend.usecases.users.SetAvatarUseCase

fun Route.usersRoot(
    getUsersUseCase: GetUsersUseCase,
    editUserUseCase: EditUserUseCase,
    setAvatarUseCase: SetAvatarUseCase
) {
    getMe(getUsersUseCase)
    getUsers(getUsersUseCase)
    editUser(editUserUseCase)
    setAvatar(setAvatarUseCase)
}