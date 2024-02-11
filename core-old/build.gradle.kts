plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    api(projects.foundation.authorization)
    api(projects.foundation.random)
    api(projects.foundation.validation)
    api(projects.foundation.pageToken)
    api(projects.foundation.time)
    api(projects.foundation.stateMachine)

    implementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(projects.foundation.testUtils)
}

tasks.withType<Test> {
    useJUnitPlatform()
}