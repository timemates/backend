package org.tomadoro.backend.providers

object SecureAccessTokenProvider : AccessTokenProvider {
    override fun provide(): org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken {
        return org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken(
            SecureRandomStringProvider.provide(128)
        )
    }
}