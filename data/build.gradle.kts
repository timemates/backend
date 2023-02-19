plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.domain)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)

    implementation(libs.litote.kmongo.async)
    implementation(libs.litote.kmongo.coroutines)

    implementation(libs.cache4k)

    implementation(libs.kotlinx.coroutines)

    implementation(libs.commons.io)

    testImplementation(libs.h2.database)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
}