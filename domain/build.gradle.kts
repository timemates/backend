plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.features.authorization)
    api(projects.features.random)
    api(projects.features.validation)
    api(projects.features.time)
    implementation(projects.features.fsm)
    implementation(projects.features.scheduler)

    implementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform()
}