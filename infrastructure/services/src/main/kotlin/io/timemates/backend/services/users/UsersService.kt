package io.timemates.backend.services.users

import io.grpc.Context
import io.grpc.stub.MetadataUtils
import io.timemates.api.common.types.StatusOuterClass
import io.timemates.api.users.UsersServiceGrpc
import io.timemates.api.users.UsersServiceGrpcKt
import io.timemates.api.users.requests.EditEmailRequestOuterClass
import io.timemates.api.users.requests.EditUserRequestOuterClass
import io.timemates.api.users.requests.GetUsersRequestOuterClass
import io.timemates.api.users.types.UserOuterClass
import io.timemates.api.users.types.user
import io.timemates.api.users.types.users
import io.timemates.backend.users.usecases.EditUserUseCase
import io.timemates.backend.users.usecases.GetUsersUseCase
import io.timemates.backend.users.usecases.SetAvatarUseCase
import kotlinx.coroutines.Dispatchers

class UsersService(
    private val editUserUseCase: EditUserUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val setAvatarUseCase: SetAvatarUseCase,
    private val mapper: UserEntitiesMapper,
) : UsersServiceGrpcKt.UsersServiceCoroutineImplBase() {
    override suspend fun getUsers(request: GetUsersRequestOuterClass.GetUsersRequest): UserOuterClass.Users {
        TODO()
    }

    override suspend fun setEmail(request: EditEmailRequestOuterClass.EditEmailRequest): StatusOuterClass.Status {
        TODO()
    }

    override suspend fun setUser(request: EditUserRequestOuterClass.EditUserRequest): StatusOuterClass.Status {
        TODO()
    }
}