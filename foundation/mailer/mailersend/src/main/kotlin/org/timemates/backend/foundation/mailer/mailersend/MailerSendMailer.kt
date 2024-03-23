import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MailerSendMailer(
    private val configuration: Configuration,
    client: HttpClient = HttpClient(CIO),
    private val isDebug: Boolean = false,
) {
    data class Configuration(
        val apiKey: String,
        val sender: String,
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

        developmentMode = isDebug

        if (isDebug)
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
    }

    suspend fun send(
        emailAddresses: List<String>,
        subject: String,
        templateId: String,
        variables: List<Variable>,
    ): Boolean = try {
        val request = EmailRequest(
            from = From(configuration.sender),
            to = emailAddresses.map(::To),
            variables = variables,
            templateId = templateId,
            subject = subject,
        )
        client.post {
            setBody(request)
        }.status.isSuccess()
    } catch (e: Throwable) {
        e.printStackTrace()
        false
    }


    @Serializable
    private data class EmailRequest(
        val subject: String,
        val from: From,
        val to: List<To>,
        val variables: List<Variable>,
        @SerialName("template_id")
        val templateId: String,
    )

    @Serializable
    private data class From(val email: String)

    @Serializable
    private data class To(val email: String)

    @Serializable
    data class Variable(val email: String, val substitutions: List<Substitution>)

    @Serializable
    data class Substitution(@SerialName("var") val varName: String, val value: String)
}