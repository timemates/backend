plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(projects.foundation.mailer)

    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.json)

    implementation(libs.kotlinx.serialization.json)

    api(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
}

