package org.tomadoro.backend.providers

interface AccessTokenProvider {
    fun provide(): org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken
}