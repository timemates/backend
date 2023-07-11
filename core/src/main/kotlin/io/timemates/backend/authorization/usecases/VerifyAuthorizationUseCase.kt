package io.timemates.backend.authorization.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.validation.createOrThrow
import com.timemates.random.RandomProvider
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.repositories.VerificationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.users.repositories.UsersRepository
import kotlin.time.Duration.Companion.days

class VerifyAuthorizationUseCase(
    private val verifications: VerificationsRepository,
    private val authorizations: AuthorizationsRepository,
    private val randomProvider: RandomProvider,
    private val users: UsersRepository,
    private val timeProvider: TimeProvider,
) {
    suspend fun execute(
        verificationToken: VerificationHash,
        code: VerificationCode,
    ): Result {
        val verification = verifications.getVerification(verificationToken)
            ?: return Result.NotFound

        return when (verification.attempts.int) {
            in 1..3 -> {
                if (verification.code == code) {
                    val accessToken = AccessHash.createOrThrow(
                        randomProvider.randomHash(AccessHash.SIZE)
                    )
                    val refreshToken = RefreshHash.createOrThrow(
                        randomProvider.randomHash(AccessHash.SIZE)
                    )
                    val metadata = verification.clientMetadata

                    val userId = users.getUserIdByEmail(verification.emailAddress)

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

        data object NotFound : Result()
    }
}