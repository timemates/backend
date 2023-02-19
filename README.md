# TimeMates

The repository with source code of TimeMates backend.

## Setup

If you want to host it by yourself, follow next instructions:
- Get latest release by building artifact with shadowjar or by getting it from [releases](https://github.com/timemates/backend/releases).
- Install [Java](https://openjdk.org/)
- Setup [environment](#Environment) (optional)
- Run application with `java -jar ..`

### Environment

To run this application on your own host you need to provide next env variables:

- `timemates.server.port` – port on which server will run (`8080` by default)
- `timemates.database.url` – url to postgres database
- `timemates.database.user` – postgres user
- `timemates.database.user.password` – postgres user's password
- `timemates.files.path` – path to directory, where uploaded files will be stored
- `timemates.smtp.host` – SMTP host of mailer
- `timemates.smtp.port` – SMTP port of mailer
- `timemates.smtp.user` – SMTP user of mailer
- `timemates.smtp.user.passowrd` – SMTP user's password of mailer
- `timemates.smtp.sender` – email of SMTP mailer

> **Note**
> 
> You can also use java arguments to set it [it](application/src/main/kotlin/io/timemates/backend/application/Application.kt) up.

## Deploy

To publish .jar to your own server use [:application:deploy](application/build.gradle.kts#L42) tasks.
It will appear if you have `timemates.host` env variable. Also, you should have next variables:

#### Required
- `timemates.host`: server address
- `timemates.user`: ssh user
- `timemates.password`: ssh user's password
- `timemates.prod.destination`: path where jar will be uploaded.
- `timemates.prod.serviceName`: service name (systemd) to reload.
- `timemates.archiveName`: name of the jar file.

#### Optional
- `timemates.knownHostsFilePath`: path to file with known hosts.

### Database

Backend creates and migrates database by itself (if it's release version), 
so that no need in your own setup or migration.
