package org.timemates.backend.auth.domain.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.repositories.UsersRepository
import org.timemates.backend.auth.domain.repositories.VerificationsRepository
import org.timemates.backend.foundation.authorization.Scope
import org.timemates.backend.types.auth.Authorization
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.types.auth.value.VerificationHash
import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserName
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe
import kotlin.time.Duration.Companion.days

class ConfigureNewAccountUseCase(
    private val usersRepository: UsersRepository,
    private val authorizations: AuthorizationsRepository,
    private val verifications: VerificationsRepository,
    private val timeProvider: TimeProvider,
    private val randomProvider: RandomProvider,
) {
    @OptIn(ValidationDelicateApi::class)
    suspend fun execute(
        verificationToken: VerificationHash,
        userName: UserName,
        shortBio: UserDescription?,
    ): Result {
        val verification = verifications.getVerification(verificationToken)
            .getOrElse { throwable -> return Result.Failure(throwable) }
            ?.takeIf { it.isConfirmed }
            ?: return Result.NotFound

        val currentTime = timeProvider.provide()

        val accessHash = AccessHash.createUnsafe(randomProvider.randomHash(AccessHash.SIZE))
        val refreshHash = RefreshHash.createUnsafe(randomProvider.randomHash(RefreshHash.SIZE))
        val expiresAt = currentTime + 30.days
        val metadata = verification.clientMetadata

        val id = usersRepository.create(
            emailAddress = verification.emailAddress,
            userName = userName,
            userDescription = shortBio,
            creationTime = timeProvider.provide()
        ).getOrElse { throwable -> return Result.Failure(throwable) }

        authorizations.create(
            userId = id,
            accessToken = accessHash,
            refreshToken = refreshHash,
            expiresAt = expiresAt,
            creationTime = currentTime,
            clientMetadata = metadata,
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
                clientMetadata = metadata,
            )
        )
    }

    sealed class Result {
        data object NotFound : Result()
        data class Success(val authorization: Authorization) : Result()
        data class Failure(val throwable: Throwable) : Result()
    }
}