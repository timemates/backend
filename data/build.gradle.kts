plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.features.scheduler)
    implementation(projects.features.exposedUtils)
    implementation(projects.features.fsm)
    implementation(projects.features.pageToken)

    implementation(projects.features.smtpMailer)

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