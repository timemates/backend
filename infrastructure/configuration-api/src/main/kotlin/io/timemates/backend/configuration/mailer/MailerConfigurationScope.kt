package io.timemates.backend.configuration.mailer

import io.timemates.backend.configuration.annotations.TimeMatesDsl

/**
 * This interface represents the configuration scope for a mailer.
 * It provides methods for configuring the mailer and SMTP settings.
 *
 * @since 1.0
 */
@TimeMatesDsl
public interface MailerConfigurationScope {

    /**
     * Configures the mailer with the provided settings using the specified block.
     * Only one implementation is allowed.
     *
     * ```kotlin
     * mailerSend {
     *     apiKey = "your-api-key"
     *     sender = "sender@example.com"
     *     confirmationTemplateId = "template-id"
     *     supportEmail = "support@example.com"
     * }
     * ```
     *
     * @param block the block of code to configure the mailer
     * @since 1.0
     */
    public fun mailerSend(block: MailerSendScope.() -> Unit)

    /**
     * Configures the SMTP settings for the mailer using the specified block.
     * Only one implementation is allowed.
     *
     * ```kotlin
     * smtp {
     *     host = "smtp.example.com"
     *     port = 587
     *     user = "smtp-user"
     *     password = "smtp-password"
     *     confirmationSender = "noreply@example.com"
     * }
     * ```
     *
     * @param block the block of code to configure the SMTP settings
     * @since 1.0
     */
    public fun smtp(block: MailerSendScope.() -> Unit)

    /**
     * This interface represents the configuration scope for the mailer send settings.
     *
     * [API Reference](https://developers.mailersend.com/api/v1/email.html#send-an-email)
     * @since 1.0
     */
    @TimeMatesDsl
    public interface MailerSendScope {
        /**
         * The API key for the mailer.
         *
         * @since 1.0
         */
        public var apiKey: String

        /**
         * The sender's email address.
         *
         * @since 1.0
         */
        public var sender: String

        /**
         * The ID of the confirmation template.
         *
         * @since 1.0
         */
        public var confirmationTemplateId: String

        /**
         * The support email address.
         *
         * @since 1.0
         */
        public var supportEmail: String
    }

    /**
     * This interface represents the configuration scope for the SMTP settings.
     *
     * @since 1.0
     */
    @TimeMatesDsl
    public interface SmtpScope {
        /**
         * The SMTP host address.
         *
         * @since 1.0
         */
        public var host: String

        /**
         * The SMTP port number.
         *
         * @since 1.0
         */
        public var port: Int

        /**
         * The SMTP user.
         *
         * @since 1.0
         */
        public var user: Int

        /**
         * The SMTP password.
         *
         * @since 1.0
         */
        public var password: String

        /**
         * The sender's email address for confirmation emails.
         *
         * @since 1.0
         */
        public var confirmationSender: String
    }
}