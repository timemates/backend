package org.timemates.api.rsocket.users

import com.google.protobuf.Empty
import io.rsocket.kotlin.RSocketError
import org.timemates.api.rsocket.internal.createOrFail
import org.timemates.api.rsocket.internal.getAuthorization
import org.timemates.api.rsocket.internal.internalFailure
import org.timemates.api.users.requests.EditEmailRequest
import org.timemates.api.users.requests.EditUserRequest
import org.timemates.api.users.requests.GetUsersRequest
import org.timemates.api.users.types.Users
import org.timemates.backend.types.users.Avatar
import org.timemates.backend.types.users.User
import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.types.users.value.UserName
import org.timemates.backend.users.domain.usecases.EditUserUseCase
import org.timemates.backend.users.domain.usecases.GetUsersUseCase
import org.timemates.api.users.UsersService as AbstractUsersService

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

        return Users {
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
    override suspend fun setUser(request: EditUserRequest): Empty {
        val result = editUserUseCase.execute(
            getAuthorization(),
            User.Patch(
                name = UserName.createOrFail(request.name),
                description = UserDescription.createOrFail(request.description),
                avatar = Avatar.GravatarId.createOrFail(request.gravatarId),
            )
        )

        return when (result) {
            EditUserUseCase.Result.Success -> Empty.Default
            EditUserUseCase.Result.Failed -> internalFailure()
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