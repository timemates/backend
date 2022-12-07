package org.tomadoro.backend.providers

fun interface RefreshTokenProvider {
    fun provide(): org.tomadoro.backend.repositories.AuthorizationsRepository.RefreshToken
}