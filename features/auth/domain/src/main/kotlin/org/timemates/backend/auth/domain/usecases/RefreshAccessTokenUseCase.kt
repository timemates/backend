package org.timemates.backend.auth.domain.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.types.auth.Authorization
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe
import kotlin.time.Duration.Companion.days

class RefreshAccessTokenUseCase(
    private val randomProvider: RandomProvider,
    private val authorizations: AuthorizationsRepository,
    private val time: TimeProvider,
) {
    @OptIn(ValidationDelicateApi::class)
    suspend fun execute(
        refreshToken: RefreshHash,
    ): Result {
        return Result.Success(
            authorizations.renew(
                refreshToken = refreshToken,
                newAccessHash = AccessHash.createUnsafe(randomProvider.randomHash(AccessHash.SIZE)),
                newRefreshHash = RefreshHash.createUnsafe(randomProvider.randomHash(RefreshHash.SIZE)),
                expiresAt = time.provide() + 30.days,
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