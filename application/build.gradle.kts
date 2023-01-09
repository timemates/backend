plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    application
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(projects.useCases)
    implementation(projects.integrations.kafkaUpdates)
    implementation(projects.integrations.localizedTime)
    implementation(projects.integrations.secureRandomString)
    implementation(projects.integrations.smtpEmails)
    implementation(projects.integrations.postgresqlRepositories)
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
