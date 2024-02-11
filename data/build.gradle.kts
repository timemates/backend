plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.core)
    implementation(projects.foundation.exposedUtils)
    implementation(projects.foundation.stateMachine)
    implementation(projects.foundation.pageToken)
    implementation(projects.foundation.hashing)
    testImplementation(projects.foundation.testUtils)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.json)

    implementation(projects.foundation.smtpMailer)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)

    implementation(libs.cache4k)

    implementation(libs.kotlinx.coroutines)

    implementation(libs.commons.io)

    testImplementation(libs.h2.database)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
}