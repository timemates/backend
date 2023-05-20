package io.timemates.backend.application.constants

internal object EnvironmentConstants {
    private const val TIME_MATES_PREFIX = "timemates"
    private const val MAILER_SEND_PREFIX = "mailersend"
    const val SMTP_PREFIX = "smtp"

    const val APPLICATION_PORT = "application.port"

    // Database
    const val DATABASE_URL = "$TIME_MATES_PREFIX.database.url"
    const val DATABASE_PORT = "$TIME_MATES_PREFIX.database.port"
    const val DATABASE_USER = "$TIME_MATES_PREFIX.database.user"
    const val DATABASE_USER_PASSWORD = "$TIME_MATES_PREFIX.database.user.password"

    // SMTP
    const val SMTP_HOST = "$TIME_MATES_PREFIX.smtp.host"
    const val SMTP_PORT = "$TIME_MATES_PREFIX.smtp.port"
    const val SMTP_USER = "$TIME_MATES_PREFIX.smtp.user"
    const val SMTP_USER_PASSWORD = "$TIME_MATES_PREFIX.smtp.user.password"
    const val SMTP_SENDER_ADDRESS = "$TIME_MATES_PREFIX.smtp.sender.address"

    // MailerSend
    const val MAILER_SEND_API_KEY = "$MAILER_SEND_PREFIX.apiKey"
    const val MAILER_SEND_SENDER = "$MAILER_SEND_PREFIX.sender"
    const val MAILER_SEND_RECIPIENT = "$MAILER_SEND_PREFIX.recipient"
    const val MAILER_SEND_CONFIRMATION_TEMPLATE = "$MAILER_SEND_PREFIX.templates.confirmation"

    // Other constants
    const val FILES_PATH = "$TIME_MATES_PREFIX.files.path"

}