package io.timemates.backend.application.constants

internal object EnvironmentConstants {
    private const val TIME_MATES_PREFIX = "TIMEMATES"
    private const val MAILERSEND_PREFIX = "MAILERSEND"

    const val GRPC_PORT = "${TIME_MATES_PREFIX}_GRPC_PORT"
    const val RSOCKET_PORT = "${TIME_MATES_PREFIX}_RSOCKET_PORT"

    // Database
    const val DATABASE_URL = "${TIME_MATES_PREFIX}_DATABASE_URL"
    const val DATABASE_USER = "${TIME_MATES_PREFIX}_DATABASE_USER"
    const val DATABASE_USER_PASSWORD = "${TIME_MATES_PREFIX}_DATABASE_USER_PASSWORD"

    // SMTP
    const val SMTP_HOST = "${TIME_MATES_PREFIX}_SMTP_HOST"
    const val SMTP_PORT = "${TIME_MATES_PREFIX}_SMTP_PORT"
    const val SMTP_USER = "${TIME_MATES_PREFIX}_SMTP_USER"
    const val SMTP_USER_PASSWORD = "${TIME_MATES_PREFIX}_SMTP_USER_PASSWORD"
    const val SMTP_SENDER_ADDRESS = "${TIME_MATES_PREFIX}_SMTP_SENDER_ADDRESS"

    // MailerSend
    const val MAILERSEND_API_KEY = "${MAILERSEND_PREFIX}_API_KEY"
    const val MAILERSEND_SENDER = "${MAILERSEND_PREFIX}_SENDER"
    const val MAILERSEND_CONFIRMATION_TEMPLATE = "${MAILERSEND_PREFIX}_CONFIRMATION_TEMPLATE"
    const val MAILERSEND_SUPPORT_EMAIL = "${MAILERSEND_PREFIX}_SUPPORT_EMAIL"

    // Other constants
    const val FILES_PATH = "${TIME_MATES_PREFIX}_FILES_PATH"
}
