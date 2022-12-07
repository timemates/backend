# ⚙️ Tomadoro Backend

The repository with source code of tomadoro backend.

## 🔭 Setup

If you want to host it by yourself, follow next instructions.

## 🦄 Environment

To run this application on your own host you need to provide next env variables:

- `SERVER_PORT` – port on which server will run (`8080` by default)
- `DATABASE_URL` – url to postgres database
- `DATABASE_USER` – postgres user
- `DATABASE_PASSWORD` – postgres user's password

## 🔑 Deploy

To publish .jar to your own server use [:application:deploy](application/build.gradle.kts#L42) tasks.
It will appear if you have `tomadoro.host` env variable. Also, you should have next variables:

- `tomadoro.host`: server address
- `tomadoro.user`: ssh user
- `tomadoro.password`: ssh user's password
- `tomadoro.prod.destination`: path where jar will be uploaded.
- `tomadoro.prod.serviceName`: service name (systemd) to reload.
- `tomadoro.archiveName`: name of the jar file.
- `tomadoro.knownHostsFilePath`: path to file with known hosts.

## 📦 Database

Backend creates and migrating database by itself (if it's release version), 
so that no need in your own setuping or migrating.
