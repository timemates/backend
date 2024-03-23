package org.timemates.backend.foundation.mailer.logging

import java.io.OutputStream
import java.io.PrintStream

class LoggingMailer(private val printStream: PrintStream = System.out) {
    fun send(
        emailAddress: String,
        subject: String,
        body: String,
    ) {
        val output = buildString {
            appendLine("---------------")
            appendLine("New email has been sent")
            appendLine("---------------")
            appendLine("Email address: $emailAddress")
            appendLine("Subject: $subject")
            appendLine("Body: $body")
            append("---------------")
        }
        printStream.println(output)
    }
}