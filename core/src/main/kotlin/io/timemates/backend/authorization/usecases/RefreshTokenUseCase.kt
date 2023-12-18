package io.timemates.backend.authorization.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.validation.createOrThrowInternally
import kotlin.time.Duration.Companion.days

class RefreshTokenUseCase(
    private val randomProvider: RandomProvider,
    private val authorizations: AuthorizationsRepository,
    private val time: TimeProvider,
) : UseCase {
    suspend fun execute(
        refreshToken: RefreshHash,
    ): Result {
        return Result.Success(
            authorizations.renew(
                refreshToken,
                AccessHash.createOrThrowInternally(randomProvider.randomHash(AccessHash.SIZE)),
                time.provide() + 30.days
            ) ?: return Result.InvalidAuthorization
        )
    }

    sealed interface Result {
        data object InvalidAuthorization : Result

        @JvmInline
        value class Success(val auth: Authorization) :
            Result
    }
}