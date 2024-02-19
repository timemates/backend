import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.shadow.jar)
    application
}

dependencies {
    implementation(projects.features.auth.domain)
    implementation(projects.features.auth.dependencies)

    implementation(projects.features.users.domain)
    implementation(projects.features.users.dependencies)

    implementation(projects.features.timers.domain)
    implementation(projects.features.timers.dependencies)

    implementation(projects.core.types)


    implementation(projects.foundation.time)
    implementation(projects.foundation.random)
    implementation(projects.foundation.cliArguments)

    implementation(projects.infrastructure.rsocketApi)
    implementation(libs.kotlinx.coroutines)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)

    implementation(libs.postgresql.driver)
    implementation(libs.h2.database)

    implementation(libs.koin.core)
    implementation(libs.logback.classic)
}

application {
    mainClass.set("org.timemates.backend.application.ApplicationKt")
}


tasks.withType<ShadowJar> {
    archiveBaseName.set("application")
    archiveClassifier.set("")
}
