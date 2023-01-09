plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.exposed.core)
    testImplementation(libs.exposed.jdbc)

    testImplementation(projects.integrations.postgresqlRepositories)
    testImplementation(projects.integrations.secureRandomString)
    testImplementation(projects.useCases)

    testImplementation(libs.h2.database)
    testImplementation(libs.junit.kotlin)
}

tasks.withType<Test> {
    useJUnit()
}