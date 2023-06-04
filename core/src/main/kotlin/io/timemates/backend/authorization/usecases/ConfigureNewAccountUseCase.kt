package io.timemates.backend.authorization.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.validation.createOrThrow
import com.timemates.random.RandomProvider
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.repositories.VerificationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserName
import kotlin.time.Duration.Companion.days

class ConfigureNewAccountUseCase(
    private val users: UsersRepository,
    private val authorizations: AuthorizationsRepository,
    private val verifications: VerificationsRepository,
    private val timeProvider: TimeProvider,
    private val randomProvider: RandomProvider,
) {
    suspend fun execute(
        verificationToken: VerificationHash,
        userName: UserName,
        shortBio: UserDescription?,
    ): Result {
        val verification = verifications.getVerification(verificationToken)
            ?.takeIf { it.isConfirmed }
            ?: return Result.NotFound

        val currentTime = timeProvider.provide()

        val accessHash = AccessHash.createOrThrow(randomProvider.randomHash(AccessHash.SIZE))
        val refreshHash = RefreshHash.createOrThrow(randomProvider.randomHash(RefreshHash.SIZE))
        val expiresAt = currentTime + 30.days

        val id = users.createUser(verification.emailAddress, userName, shortBio, timeProvider.provide())
        authorizations.create(
            id,
            accessHash,
            refreshHash,
            expiresAt,
            currentTime,
        )

        verifications.remove(verificationToken)

        return Result.Success(
            Authorization(
                userId = id,
                accessHash = accessHash,
                refreshAccessHash = refreshHash,
                scopes = listOf(Scope.All),
                expiresAt = expiresAt,
                createdAt = currentTime,
            )
        )
    }

    sealed class Result {
        data object NotFound : Result()
        class Success(val authorization: Authorization) : Result()
    }
}