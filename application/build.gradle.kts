plugins {
    id(Deps.Plugins.Configuration.Kotlin.Jvm)
    id(Deps.Plugins.Serialization.Id)
    id(Deps.Plugins.Deploy.Id)
}

dependencies {
    implementation(Deps.Libs.Ktor.Server.Core)
    implementation(Deps.Libs.Ktor.Server.Cio)
    implementation(Deps.Libs.Ktor.Server.Openapi)
//    implementation(Deps.Libs.Ktor.Server.RequestValidation)
    implementation(Deps.Libs.Ktor.Server.ContentNegotiation)
    implementation(Deps.Libs.Ktor.Json)
    implementation(Deps.Libs.Kotlinx.Serialization)
    implementation(Deps.Libs.Kotlinx.Coroutines)
    implementation(project(Deps.Modules.UseCases))
    implementation(project(Deps.Modules.Domain))
    implementation(project(Deps.Modules.Adapters.RepositoriesIntegration))
    implementation(project(Deps.Modules.Adapters.TimeIntegration))
    implementation(project(Deps.Modules.Adapters.TokensIntegration))
    implementation(project(Deps.Modules.Adapters.GoogleAuthIntegration))
    implementation(project(Deps.Modules.Adapters.CodesIntegration))
    implementation(Deps.Libs.Exposed.Core)
    implementation(Deps.Libs.Exposed.Jdbc)
    implementation(Deps.Libs.Postgres.Jdbc)
    implementation(Deps.Libs.Ktor.Server.Core)
    implementation(Deps.Libs.Ktor.Server.HostCommonJvm)
    implementation(Deps.Libs.Ktor.Server.StatusPages)
    implementation(Deps.Libs.Ktor.Server.CORS)
    implementation(Deps.Libs.Ktor.Server.WebSockets)
    implementation(Deps.Libs.Ktor.Server.CallLogging)
}

deploy {
    if (System.getenv("host") != null) {
        default {
            host = System.getenv("tomadoro.host")
            user = System.getenv("tomadoro.user")
            password = System.getenv("tomadoro.password")
            knownHostsFile = System.getenv("tomadoro.knownHostsFilePath")
            archiveName = System.getenv("tomadoro.archiveName")

            mainClass = "org.tomadoro.backend.application.MainKt"
        }

        target("production") {
            destination = System.getenv("tomadoro.prod.destination")
            serviceName = System.getenv("tomadoro.prod.serviceName")
        }
    }
}

application {
    mainClass.set("org.tomadoro.backend.application.MainKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
    }
}