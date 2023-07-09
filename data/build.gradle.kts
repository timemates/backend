plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.core)
    implementation(projects.common.scheduler)
    implementation(projects.common.exposedUtils)
    implementation(projects.common.stateMachine)
    implementation(projects.common.pageToken)
    implementation(projects.common.hashing)
    testImplementation(projects.common.testUtils)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.json)

    implementation(projects.common.smtpMailer)

    implementation(libs.kotlinx.serialization)

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