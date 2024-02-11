plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(projects.foundation.time)
    implementation(projects.foundation.pageToken)

    api(projects.core.types)
    implementation(projects.core.types.authIntegration)
}

tasks.withType<Test> {
    useJUnitPlatform()
}