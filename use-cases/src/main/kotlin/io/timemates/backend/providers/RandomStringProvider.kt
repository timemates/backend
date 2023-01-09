package io.timemates.backend.providers

import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.VerificationsRepository

interface RandomStringProvider {
    /**
     * Generates random string for given [size].
     */
    fun provide(size: Int): String
}

fun RandomStringProvider.provideAccessToken(): AuthorizationsRepository.AccessToken =
    AuthorizationsRepository.AccessToken(provide(AuthorizationsRepository.AccessToken.SIZE))

fun RandomStringProvider.provideVerificationCode(): VerificationsRepository.Code =
    VerificationsRepository.Code(provide(VerificationsRepository.Code.SIZE))

fun RandomStringProvider.provideInviteCode(): TimerInvitesRepository.Code =
    TimerInvitesRepository.Code(provide(TimerInvitesRepository.Code.SIZE))

fun RandomStringProvider.provideVerificationToken(): VerificationsRepository.VerificationToken =
    VerificationsRepository.VerificationToken(provide(VerificationsRepository.VerificationToken.SIZE))

fun RandomStringProvider.provideRefreshToken(): AuthorizationsRepository.RefreshToken =
    AuthorizationsRepository.RefreshToken(provide(AuthorizationsRepository.RefreshToken.SIZE))