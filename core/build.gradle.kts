plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    api(projects.common.authorization)
    api(projects.common.random)
    api(projects.common.validation)
    api(projects.common.pageToken)
    api(projects.common.time)
    api(projects.common.stateMachine)

    implementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(projects.common.testUtils)
}

tasks.withType<Test> {
    useJUnitPlatform()
}