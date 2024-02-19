# SMTP Mailer

The `smtp-mailer` module provides a simple interface for sending emails using the Simple Mail Transfer Protocol (SMTP). It allows you to easily configure and send emails through an SMTP server.

## Features

- **SMTP Configuration**: The module allows you to specify the SMTP server details, including the host, port,
user, and password (optional). These settings are used to establish a connection with the SMTP server for sending 
emails.

- **Email Composition**: You can use the `send` function provided by the `SMTPMailer` class to compose and 
send emails. It accepts the recipient's email address, subject, and body as parameters. The email can be sent as plain text.

- **Email Validation**: Before sending an email, the module validates the email address to ensure its 
correctness. If the email address is invalid, the sending process is aborted.

## Usage

To use the `smtp-mailer` module, follow these steps:

1. Create an instance of the `SMTPMailer` class by providing the SMTP server details, including the host, port, user, 
password (if required), and the sender's email address.

2. Call the `send` function on the `SMTPMailer` instance, providing the recipient's email address, subject, and body as parameters.

3. The module will validate the email address and proceed to send the email using the configured SMTP server.
The function returns a boolean value indicating the success of the sending process.

```kotlin
val mailer = SMTPMailer(host, port, user, password, sender)

val recipient = "recipient@example.com"
val subject = "Hello, World!"
val body = "This is the body of the email."

val isSent = mailer.send(recipient, subject, body)

if (isSent) {
    println("Email sent successfully.")
} else {
    println("Failed to send email.")
}
```