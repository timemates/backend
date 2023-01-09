package io.timemates.backend.usecases.auth

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.providers.RandomStringProvider
import io.timemates.backend.providers.provideAccessToken
import io.timemates.backend.providers.provideRefreshToken
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.repositories.VerificationsRepository
import kotlin.time.Duration.Companion.days

class VerifyAuthorizationUseCase(
    private val verifications: VerificationsRepository,
    private val authorizations: AuthorizationsRepository,
    private val randomStringProvider: RandomStringProvider,
    private val users: UsersRepository,
    private val timeProvider: CurrentTimeProvider
) {
    suspend operator fun invoke(
        verificationToken: VerificationsRepository.VerificationToken,
        code: VerificationsRepository.Code
    ): Result {
        val verification = verifications.getVerification(verificationToken)
            ?: return Result.NotFound

        return when {
            verification.attempts.int < 3 -> {
                if(verification.code == code) {
                    verifications.remove(verificationToken)
                    val accessToken = randomStringProvider.provideAccessToken()
                    val refreshToken = randomStringProvider.provideRefreshToken()

                    val user = users.getUser(verification.emailAddress)

                    return if(user != null) {
                        val expireTime = timeProvider.provide() + 30.days

                        authorizations.create(
                            user.userId,
                            accessToken,
                            refreshToken,
                            expireTime
                        )
                        Result.Success.ExistsAccount(
                            AuthorizationsRepository.Authorization(
                                user.userId,
                                accessToken,
                                refreshToken,
                                expireTime
                            )
                        )
                    } else {
                        verifications.markConfirmed(verificationToken)
                        Result.Success.NewAccount
                    }
                }

                verifications.addAttempt(verificationToken)
                Result.AttemptFailed
            }
            else -> Result.AttemptsExceed
        }
    }

    sealed class Result {
        sealed class Success : Result() {
            object NewAccount : Success()
            class ExistsAccount(
                val authorization: AuthorizationsRepository.Authorization
            ) : Success()
        }

        object AttemptsExceed : Result()
        object AttemptFailed : Result()

        object NotFound : Result()
    }
}