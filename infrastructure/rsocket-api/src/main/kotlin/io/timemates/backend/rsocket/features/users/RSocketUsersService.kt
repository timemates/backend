package io.timemates.backend.rsocket.features.users

import io.timemates.backend.rsocket.features.authorization.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.serializable.types.users.SerializableUser
import io.timemates.backend.serializable.types.users.SerializableUserPatch
import io.timemates.backend.serializable.types.users.serializable
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.usecases.EditUserUseCase
import io.timemates.backend.users.usecases.GetUsersUseCase

class RSocketUsersService(
    private val editUserUseCase: EditUserUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val mapper: RSocketUsersMapper,
) : RSocketService {
    suspend fun getUsers(ids: List<Long>): List<SerializableUser> {
        val result = getUsersUseCase.execute(ids.map { id -> UserId.createOrFail(id) })

        return when (result) {
            is GetUsersUseCase.Result.Success -> result.collection.map(User::serializable)
        }
    }

    suspend fun editUser(patch: SerializableUserPatch): Unit = provideAuthorizationContext {
        val result = editUserUseCase.execute(mapper.toDomainUserPatch(patch))

        return when (result) {
            is EditUserUseCase.Result.Success -> {}
        }
    }
}