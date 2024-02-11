plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(projects.features.users.domain)
    implementation(projects.foundation.exposedUtils)
    implementation(projects.foundation.hashing)

    implementation(libs.exposed.core)
    implementation(libs.cache4k)
}