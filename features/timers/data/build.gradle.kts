plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(projects.features.timers.domain)
    implementation(projects.foundation.exposedUtils)
    implementation(projects.foundation.hashing)
    implementation(projects.foundation.pageToken)
    implementation(projects.foundation.stateMachine)


    implementation(libs.kotlinx.serialization.json)
    implementation(libs.exposed.core)
    implementation(libs.cache4k)
}