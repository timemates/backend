plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.exposed.core)
    api(libs.exposed.jdbc)

    implementation(projects.useCases)
    testImplementation(libs.h2.database)
    testImplementation(libs.junit.jupiter)
}

tasks.withType<Test> {
    useJUnitPlatform()
}