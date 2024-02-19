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
import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.auth.value.VerificationHash
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe
import kotlin.time.Duration.Companion.days

class VerifyAuthorizationUseCase(
    private val verifications: VerificationsRepository,
    private val authorizations: AuthorizationsRepository,
    private val randomProvider: RandomProvider,
    private val usersRepository: UsersRepository,
    private val timeProvider: TimeProvider,
) {
    @OptIn(ValidationDelicateApi::class)
    suspend fun execute(
        verificationToken: VerificationHash,
        code: VerificationCode,
    ): Result {
        val verification = verifications.getVerification(verificationToken)
            .getOrElse { throwable -> return Result.Failure(throwable) }
            ?: return Result.NotFound

        return when (verification.attempts.int) {
            in 1..3 -> {
                if (verification.code == code) {
                    val accessToken = AccessHash.createUnsafe(
                        randomProvider.randomHash(AccessHash.SIZE)
                    )
                    val refreshToken = RefreshHash.createUnsafe(
                        randomProvider.randomHash(AccessHash.SIZE)
                    )
                    val metadata = verification.clientMetadata

                    val userId = usersRepository.get(verification.emailAddress)
                        .getOrElse { throwable -> return Result.Failure(throwable) }

                    return if (userId != null) {
                        val expireTime = timeProvider.provide() + 30.days

                        authorizations.create(
                            userId,
                            accessToken,
                            refreshToken,
                            expireTime,
                            timeProvider.provide(),
                            metadata,
                        )

                        Result.Success.ExistsAccount(
                            Authorization(
                                userId,
                                accessToken,
                                refreshToken,
                                listOf(Scope.All),
                                expireTime,
                                timeProvider.provide(),
                                metadata,
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
            data object NewAccount : Success()
            class ExistsAccount(
                val authorization: Authorization,
            ) : Success()
        }

        data object AttemptsExceed : Result()
        data object AttemptFailed : Result()

        data class Failure(val throwable: Throwable) : Result()

        data object NotFound : Result()
    }
}