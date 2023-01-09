package io.timemates.backend.usecases.auth

import io.timemates.backend.types.value.ShortBio
import io.timemates.backend.types.value.UserName
import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.providers.RandomStringProvider
import io.timemates.backend.providers.provideAccessToken
import io.timemates.backend.providers.provideRefreshToken
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.repositories.VerificationsRepository
import kotlin.time.Duration.Companion.days

class ConfigureNewAccountUseCase(
    private val users: UsersRepository,
    private val authorizations: AuthorizationsRepository,
    private val verifications: VerificationsRepository,
    private val timeProvider: CurrentTimeProvider,
    private val randomStringProvider: RandomStringProvider
) {
    suspend operator fun invoke(
        verificationToken: VerificationsRepository.VerificationToken,
        userName: UserName,
        shortBio: ShortBio?
    ): Result {
        val verification = verifications.getVerification(verificationToken)
            ?.takeIf { it.isConfirmed }
            ?: return Result.NotFound

        val accessToken = randomStringProvider.provideAccessToken()
        val refreshToken = randomStringProvider.provideRefreshToken()
        val expiresAt = timeProvider.provide() + 30.days

        val id = users.createUser(verification.emailAddress, userName, shortBio, timeProvider.provide())
        authorizations.create(id, accessToken, refreshToken, expiresAt)
        return Result.Success(
            AuthorizationsRepository.Authorization(
                id, accessToken, refreshToken, expiresAt
            )
        )
    }

    sealed class Result {
        object NotFound : Result()
        class Success(val authorization: AuthorizationsRepository.Authorization) : Result()
    }
}