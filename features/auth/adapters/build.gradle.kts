plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.features.auth.domain)
    implementation(projects.features.users.domain)

    implementation(projects.foundation.random)
    implementation(projects.foundation.smtpMailer)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.json)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
}