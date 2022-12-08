package org.tomadoro.backend.usecases.auth

import org.tomadoro.backend.domain.UserName
import org.tomadoro.backend.google.auth.GoogleClient
import org.tomadoro.backend.providers.AccessTokenProvider
import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.providers.RefreshTokenProvider
import org.tomadoro.backend.repositories.AuthorizationsRepository
import org.tomadoro.backend.repositories.LinkedSocialsRepository
import org.tomadoro.backend.repositories.UsersRepository
import java.time.Duration

class AuthViaGoogleUseCase(
    private val linkedSocials: LinkedSocialsRepository,
    private val users: UsersRepository,
    private val tokensProvider: AccessTokenProvider,
    private val authorizations: AuthorizationsRepository,
    private val time: CurrentTimeProvider,
    private val refreshTokens: RefreshTokenProvider,
    private val googleClient: GoogleClient
) {
    suspend operator fun invoke(code: String): Result {
        val accessTokenResult = googleClient.getAccessToken(code, "about:blank")
            ?: return Result.InvalidAuthorization
        val user = googleClient.getUserProfile(accessTokenResult)
        val linked = linkedSocials.getBySocial(
            LinkedSocialsRepository.Social.Google(
                accessTokenResult.id,
                user.email
            )
        )

        return if (linked == null) {
            val id = users.createUser(UserName(user.name), null, time.provide())
            val accessToken = tokensProvider.provide()
            authorizations.create(
                id, accessToken, refreshTokens.provide(), time.provide() + Duration.ofDays(7).toMillis()
            )
            Result.Success(accessToken)
        } else {
            val accessToken = tokensProvider.provide()
            authorizations.create(
                linked,
                tokensProvider.provide(),
                refreshTokens.provide(),
                time.provide() + Duration.ofDays(7).toMillis()
            )
            Result.Success(accessToken)
        }
    }

    sealed interface Result {
        object InvalidAuthorization : Result

        @JvmInline
        value class Success(val accessToken: AuthorizationsRepository.AccessToken) :
            Result
    }
}