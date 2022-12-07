package org.tomadoro.backend.google.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HttpGoogleClient(
    private val clientId: String,
    private val clientSecret: String
) : GoogleClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override suspend fun getAccessToken(code: String, redirectUrl: String): GoogleClient.GetAccessTokenResponse? {
        val response = client.post("https://accounts.google.com/o/oauth2/token") {
            parameter("client_id", clientId)
            parameter("client_secret", clientSecret)
            parameter("redirect_uri", redirectUrl)
            parameter("code", code)
            parameter("grant_type", "authorization_code")
        }

        return if (response.status.isSuccess())
            response.body()
        else null
    }

    override suspend fun getUserProfile(getAccessTokenResponse: GoogleClient.GetAccessTokenResponse): GoogleClient.UserProfile {
        val response = client.get("https://www.googleapis.com/oauth2/v1/userinfo") {
            parameter("access_token", getAccessTokenResponse.accessToken)
            parameter("id_token", getAccessTokenResponse.idToken)
            parameter("expires_in", 3599)
            parameter("token_type", "Bearer")
        }

        return response.body()
    }
}