package org.tomadoro.backend.google.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface GoogleClient {
    suspend fun getAccessToken(
        code: String, redirectUrl: String
    ): GetAccessTokenResponse?

    suspend fun getUserProfile(
        getAccessTokenResponse: GetAccessTokenResponse
    ): UserProfile

    @Serializable
    class UserProfile(
        val email: String,
        val name: String
    )

    @Serializable
    class GetAccessTokenResponse(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("id_token")
        val idToken: String,
        val id: Long
    )
}