package io.timemates.backend.auth.domain.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.types.auth.Authorization
import io.timemates.backend.types.auth.value.AccessHash
import io.timemates.backend.types.auth.value.RefreshHash
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe
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
                refreshToken,
                AccessHash.createUnsafe(randomProvider.randomHash(AccessHash.SIZE)),
                time.provide() + 30.days,
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