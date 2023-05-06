plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.domain)
    implementation(projects.features.scheduler)
    implementation(projects.features.exposedUtils)

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