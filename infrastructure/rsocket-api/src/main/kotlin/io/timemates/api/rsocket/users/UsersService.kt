package io.timemates.api.rsocket.users

import com.google.protobuf.Empty
import io.rsocket.kotlin.RSocketError
import io.timemates.api.rsocket.internal.authorized
import io.timemates.api.rsocket.internal.createOrFail
import io.timemates.api.users.requests.EditEmailRequest
import io.timemates.api.users.requests.EditUserRequest
import io.timemates.api.users.requests.GetUsersRequest
import io.timemates.api.users.types.Users
import io.timemates.backend.users.types.Avatar
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.UserName
import io.timemates.backend.users.usecases.EditUserUseCase
import io.timemates.backend.users.usecases.GetUsersUseCase
import io.timemates.api.users.UsersService as AbstractUsersService

/**
 * UsersService class represents a service that provides operations related to users.
 *
 * @param getUsersUseCase The use case responsible for retrieving users.
 * @param editUserUseCase The use case responsible for editing user information.
 */
class UsersService(
    private val getUsersUseCase: GetUsersUseCase,
    private val editUserUseCase: EditUserUseCase,
) : AbstractUsersService() {
    /**
     * Retrieves users by the provided request.
     *
     * @param request The request containing a list of user IDs to retrieve.
     * @return A [Users] object containing the retrieved users.
     */
    override suspend fun getUsers(request: GetUsersRequest): Users {
        val result = getUsersUseCase.execute(request.userId.map { UserId.createOrFail(it) })

        return Users.create {
            users = when (result) {
                is GetUsersUseCase.Result.Success -> result.collection.map { it.rs() }
            }
        }
    }


    /**
     * Applies new profile information given in the request to authorized user.
     *
     * @see EditUserRequest
     */
    override suspend fun setUser(request: EditUserRequest): Empty = authorized {
        val result = editUserUseCase.execute(
            User.Patch(
                name = UserName.createOrFail(request.name),
                description = UserDescription.createOrFail(request.description),
                avatar = Avatar.GravatarId.createOrFail(request.gravatarId),
            )
        )

        when (result) {
            EditUserUseCase.Result.Success -> Empty.Default
        }
    }

    /**
     * Sets a new email for the current user.
     *
     * @param request The request object that contains the new email.
     * @return An instance of the Empty class.
     */
    override suspend fun setEmail(request: EditEmailRequest): Empty {
        throw RSocketError.ApplicationError("Not implemented")
    }
}