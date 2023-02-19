plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    application
}

dependencies {
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)

    implementation(projects.integrations.localizedTime)
    implementation(projects.integrations.secureRandomString)
    implementation(projects.integrations.smtpEmails)
    implementation(projects.integrations.postgresqlStorage)
    implementation(projects.integrations.localFiles)
    implementation(projects.integrations.cacheStorage)
    implementation(projects.endpoints)
}

application {
    mainClass.set("io.timemates.backend.ApplicationKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
    }
}
