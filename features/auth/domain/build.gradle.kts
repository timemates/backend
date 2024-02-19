plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(projects.foundation.time)
    implementation(projects.foundation.pageToken)
    implementation(projects.foundation.random)

    implementation(projects.core.types.authIntegration)

    api(projects.core.types)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(projects.foundation.validation.testsIntegration)
}

tasks.withType<Test> {
    useJUnitPlatform()
}