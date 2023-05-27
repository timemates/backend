package io.timemates.backend.application.constants

internal object FailureMessages {
    const val MISSING_PORT = "Please provide a valid port number."
    const val MISSING_DATABASE_URL = "Please provide a database URL."
    const val MISSING_SMTP_HOST = "You're missing the SMTP host."
    const val MISSING_SMTP_PORT = "You're missing the SMTP port."
    const val MISSING_SMTP_USER = "You're missing the SMTP user."
    const val MISSING_SMTP_SENDER = "You're missing the SMTP sender."
    const val MISSING_MAILERSEND_API_KEY = "You're missing the API key for MailerSend."
    const val MISSING_MAILERSEND_SENDER = "You're missing the author of the email (MailerSend sender)."
    const val MISSING_MAILERSEND_CONFIRMATION_TEMPLATE = "You're missing the template for confirmations for MailerSend."
    const val MISSING_MAILER = "You have not specified a mailer, please refer to documentation for correct setup."
    const val MISSING_FILES_PATH = "You're missing the files path."
}