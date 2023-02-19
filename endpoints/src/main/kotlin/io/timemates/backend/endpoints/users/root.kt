package io.timemates.backend.endpoints.users

import io.ktor.server.routing.*
import io.timemates.backend.usecases.users.EditUserUseCase
import io.timemates.backend.usecases.users.GetUsersUseCase
import io.timemates.backend.usecases.users.SetAvatarUseCase

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