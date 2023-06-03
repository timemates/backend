package io.timemates.backend.data.common.repositories

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.timemates.backend.authorization.types.Email
import io.timemates.backend.common.repositories.EmailsRepository
import io.timemates.backend.users.types.value.EmailAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonArray
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

        install(ContentNegotiation) {
            json()
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    override suspend fun send(emailAddress: EmailAddress, email: Email): Boolean = try {
        email as Email.AuthorizeEmail

        val request = EmailRequest(
            from = From(configuration.sender),
            to = listOf(To(emailAddress.string)),
            variables = listOf(
                Variable(
                    email = emailAddress.string,
                    substitutions = listOf(
                        Substitution(varName = "support_email", value = ""),
                        Substitution(varName = "confirmation.code", value = email.code.string)
                    )
                )
            ),
            templateId = configuration.confirmationTemplateId,
            subject = "Authorization confirmation"
        )
         client.post {
             setBody(request)
         }.status.isSuccess()
    } catch (e: Throwable) {
        e.printStackTrace()
        false
    }
}


@Serializable
internal data class EmailRequest(
    val subject: String,
    val from: From,
    val to: List<To>,
    val variables: List<Variable>,
    @SerialName("template_id")
    val templateId: String
)

@Serializable
internal data class From(val email: String)
@Serializable
internal data class To(val email: String)
@Serializable
internal data class Variable(val email: String, val substitutions: List<Substitution>)
@Serializable
internal data class Substitution(@SerialName("var") val varName: String, val value: String)