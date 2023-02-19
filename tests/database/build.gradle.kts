plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.exposed.core)
    testImplementation(libs.exposed.jdbc)

    testImplementation(projects.integrations.postgresqlStorage)
    testImplementation(projects.integrations.secureRandomString)
    testImplementation(projects.integrations.localizedTime)
    testImplementation(projects.application)
    testImplementation(projects.integrations.cacheStorage)
    testImplementation(projects.useCases)

    testImplementation(libs.h2.database)
    testImplementation(libs.junit.kotlin)
}

tasks.withType<Test> {
    useJUnit()
}