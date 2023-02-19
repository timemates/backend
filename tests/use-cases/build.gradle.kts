plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.junit.kotlin)

    testImplementation(projects.useCases)
    testImplementation(projects.integrations.inmemoryRepositories)
    testImplementation(projects.integrations.secureRandomString)
    testImplementation(projects.integrations.localizedTime)
}

tasks.withType<Test> {
    useJUnit()
}