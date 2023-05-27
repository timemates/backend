![Platform](https://img.shields.io/badge/platform-jvm-yellow)
![Latest GitHub release](https://img.shields.io/github/v/release/timemates/backend?include_prereleases)
![GitHub](https://img.shields.io/github/license/timemates/backend)
![GitHub issues](https://img.shields.io/github/issues/timemates/backend)
# TimeMates Backend
The repository with source code of TimeMates server on Kotlin.

> **Warning** <br>
> The current backend version may lack stability in the API and migration support. It's not production ready.

## Setup

If you want to host the application yourself, follow the instructions below:

1. Obtain the latest release by either building the artifact with ShadowJar or downloading it from the [releases](https://github.com/timemates/backend/releases) page.
2. Install [Java](https://openjdk.org/) on your system.
3. Set up the environment variables as described in the [Environment](#environment) section (optional).
4. Run the application using the following command: `java -jar <path-to-jar-file>`

### Environment

To run this application on your own host, you need to provide the following environment variables:

- `TIMEMATES_SERVER_PORT` – The port on which the server will run (default: `8080`)
- `TIMEMATES_DATABASE_URL` – The URL to the PostgreSQL database
- `TIMEMATES_DATABASE_USER` – The username for the PostgreSQL database
- `TIMEMATES_DATABASE_USER_PASSWORD` – The password for the PostgreSQL user
- `TIMEMATES_FILES_PATH` – The path to the directory where uploaded files will be stored
- `TIMEMATES_SMTP_HOST` – The SMTP host of the mailer
- `TIMEMATES_SMTP_PORT` – The SMTP port of the mailer
- `TIMEMATES_SMTP_USER` – The SMTP user of the mailer
- `TIMEMATES_SMTP_USER_PASSWORD` – The password for the SMTP user
- `TIMEMATES_SMTP_SENDER` – The email address of the SMTP mailer

> **Note** <br>
> There are two mailer implementations available: SMTP and MailerSend. Depending on your choice, you need to provide the corresponding environment variables.
>
> If using the SMTP mailer implementation, make sure to set the `TIMEMATES_SMTP_HOST`, `TIMEMATES_SMTP_PORT`, `TIMEMATES_SMTP_USER`, `TIMEMATES_SMTP_USER_PASSWORD`, and `TIMEMATES_SMTP_SENDER` variables.
>
> If using the MailerSend implementation, you should set the `MAILERSEND_API_KEY`, `MAILERSEND_SENDER`, and `MAILERSEND_CONFIRMATION_TEMPLATE` variables.
>
> Refer to the code documentation for more details on configuring the mailer implementation.

> **Note** <br>
> You can also use Java arguments to set up the application. Refer to the [source code](infrastructure/application/src/main/kotlin/io/timemates/backend/application/Application.kt) for more information.

## Docker image
The Timemates Backend Docker image is available on Docker Hub. You can pull the image and run it locally or deploy it to your server.

To pull the Docker image from Docker Hub, use the following command:
```bash
docker pull y9vad9/timemates-backend:$version
docker run --env-file .env y9vad9/timemates-backend:$version
```
Replace `$version` with the specific version or tag you want to use.

For example, to pull the latest version, use:
```bash
docker pull y9vad9/timemates-backend:latest
docker run --env-file .env y9vad9/timemates-backend:$version
```

You should also provide `DOCKER_IMAGE_PORT` environment variable for docker port. Also,
you should add all environment variables from [Environment](#environment).

### Database

Backend creates and migrates Postgresql database by itself (if it's release version),
so that no need in your own setup or migration.
