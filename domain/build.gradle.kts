plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.features.authorization)
    api(projects.features.random)
    api(projects.features.validation)
    api(projects.features.pageToken)
    api(projects.features.time)
    api(projects.features.fsm)

    implementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    implementation(projects.features.validation)
    testImplementation(projects.features.testUtils)
}

tasks.withType<Test> {
    useJUnitPlatform()
}