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

1. Obtain the latest release by either building the artifact with ShadowJar or downloading it from
   the [releases](https://github.com/timemates/backend/releases) page.
2. Install [Java](https://openjdk.org/) on your system.
3. Set up the environment variables as described in the [Environment](#environment) section (optional).
4. Run the application using the following command: `java -jar <path-to-jar-file>`

### Environment

To run this application on your own host, you need to provide the following environment variables:

- `timemates_rsocket_port` – The port on which the rsocket instance will run (default: `8080`)
- `timemates_database_url` – The URL to the PostgreSQL database
- `timemates_database_user` – The username for the PostgreSQL database
- `timemates_database_password` – The password for the PostgreSQL user
- `mailersend_api_key` – MailerSend API key
- `mailersend_sender` – MailerSend sender
- `mailersend_confirmation_template` – MailerSend template for authentication confirmation
- `mailersend_support_email` – Support email for MailerSend
- `timemates_timers_cache_size` – Cache size for timers (default: `100`)
- `timemates_users_cache_size` – Cache size for users (default: `100`)
- `timemates_auth_cache_size` – Cache size for authentication entities (default: `100`)
- `timemates_auth_cache_alive` – Maximum alive time for authentication cache in seconds (default: `300` seconds, or 5
  minutes)
- `timemates_debug` – Enable debug mode (present or not, acts as a flag)

> **Note** <br>
> You can also use Java arguments to set up the application. Refer to
> the [source code](/app/src/main/kotlin/io/timemates/backend/application/Application.kt) for more
> information.
>

## Docker image

The Timemates Backend Docker image is available on Docker Hub. You can pull the image and run it locally or deploy it to
your server.

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
