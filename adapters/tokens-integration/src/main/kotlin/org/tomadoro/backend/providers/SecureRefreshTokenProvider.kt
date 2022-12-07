package org.tomadoro.backend.providers

object SecureRefreshTokenProvider : RefreshTokenProvider {
    override fun provide(): org.tomadoro.backend.repositories.AuthorizationsRepository.RefreshToken {
        return org.tomadoro.backend.repositories.AuthorizationsRepository.RefreshToken(
            SecureRandomStringProvider.provide(128)
        )
    }
}