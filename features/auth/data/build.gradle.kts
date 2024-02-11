plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(projects.features.auth.domain)
    implementation(projects.foundation.exposedUtils)
    implementation(projects.foundation.pageToken)
    implementation(projects.foundation.hashing)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.exposed.core)
    implementation(libs.cache4k)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.exposed.jdbc)
    testImplementation(libs.h2.database)

    testImplementation(projects.foundation.validation.testsIntegration)
}