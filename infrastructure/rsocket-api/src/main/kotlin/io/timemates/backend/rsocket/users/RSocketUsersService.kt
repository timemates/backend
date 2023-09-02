package io.timemates.backend.rsocket.users

import io.timemates.backend.rsocket.authorization.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.rsocket.users.types.User
import io.timemates.backend.services.authorization.context.provideAuthorizationContext
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.usecases.EditUserUseCase
import io.timemates.backend.users.usecases.GetUsersUseCase

class RSocketUsersService(
    private val editUserUseCase: EditUserUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val mapper: RSocketUsersMapper,
) : RSocketService {
    suspend fun getUsers(ids: List<Long>): List<User> {
        val result = getUsersUseCase.execute(ids.map { id -> UserId.createOrFail(id) })

        return when (result) {
            is GetUsersUseCase.Result.Success -> result.collection.map(mapper::toRSocketUser)
        }
    }

    suspend fun editUser(patch: User.Patch): Unit = provideAuthorizationContext {
        val result = editUserUseCase.execute(patch)
    }
}