package io.timemates.backend.data.common.repositories

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.timemates.backend.authorization.types.Email
import io.timemates.backend.common.repositories.EmailsRepository
import io.timemates.backend.users.types.value.EmailAddress
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MailerSendEmailsRepository(
    private val configuration: Configuration,
    client: HttpClient = HttpClient(CIO),
) : EmailsRepository {
    data class Configuration(
        val apiKey: String,
        val sender: String,
        val confirmationTemplateId: String,
    )

    private val client = client.config {
        install(DefaultRequest) {
            bearerAuth(configuration.apiKey)
            contentType(ContentType.Application.Json)

            url("https://api.mailersend.com/v1/email")
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    override suspend fun send(emailAddress: EmailAddress, email: Email): Boolean {
        return client.post {
            setBody(buildJsonObject {
                put("to", emailAddress.string)
                put("from", configuration.sender)
                when (email) {
                    is Email.AuthorizeEmail -> {
                        put("template_id", configuration.confirmationTemplateId)
                        put("variables", buildJsonObject {
                            put("confirmation.code", email.code.string)
                        })
                    }
                }
            })
        }.status.isSuccess()
    }
}