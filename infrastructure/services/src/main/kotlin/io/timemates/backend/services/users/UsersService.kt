package io.timemates.backend.services.users

import com.google.protobuf.Empty
import com.timemates.backend.validation.createAsResult
import com.timemates.backend.validation.createOrThrow
import io.grpc.Status
import io.grpc.StatusException
import io.timemates.api.users.UsersServiceGrpcKt
import io.timemates.api.users.requests.EditEmailRequestOuterClass
import io.timemates.api.users.requests.EditUserRequestOuterClass
import io.timemates.api.users.requests.GetUsersRequestOuterClass
import io.timemates.api.users.types.UserOuterClass.Users
import io.timemates.backend.services.authorization.context.provideAuthorizationContext
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.usecases.EditUserUseCase
import io.timemates.backend.users.usecases.GetUsersUseCase
import io.timemates.backend.files.usecases.UploadFileUseCase
import io.timemates.backend.services.common.validation.createOrStatus

class UsersService(
    private val editUserUseCase: EditUserUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val mapper: UserEntitiesMapper,
) : UsersServiceGrpcKt.UsersServiceCoroutineImplBase() {
    override suspend fun getUsers(request: GetUsersRequestOuterClass.GetUsersRequest): Users {
        return when(val result = getUsersUseCase.execute(request.userIdList.map { UserId.createOrStatus(it) })) {
            is GetUsersUseCase.Result.Success -> Users.newBuilder()
                .addAllUsers(result.collection.map { mapper.toGrpcUser(it) })
                .build()
        }
    }

    override suspend fun setEmail(
        request: EditEmailRequestOuterClass.EditEmailRequest
    ): Empty = throw StatusException(Status.UNIMPLEMENTED)

    override suspend fun setUser(
        request: EditUserRequestOuterClass.EditUserRequest
    ): Empty = provideAuthorizationContext {
        val patch = mapper.toGrpcUserPatch(request)

        when (editUserUseCase.execute(patch)) {
            EditUserUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }
}