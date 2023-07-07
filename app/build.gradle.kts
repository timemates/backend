import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.shadow.jar)
    application
}

dependencies {
    implementation(projects.data)
    implementation(projects.core)
    implementation(projects.infrastructure.grpcApi)
    implementation(projects.common.smtpMailer)
    implementation(projects.common.cliArguments)

    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)

    implementation(libs.postgresql.driver)
    implementation(libs.h2.database)

    implementation(libs.grpc.netty)
    implementation(libs.grpc.services)

    implementation(libs.koin.core)

    implementation(libs.logback.classic)
}

application {
    mainClass.set("io.timemates.backend.application.ApplicationKt")
}


tasks.withType<ShadowJar> {
    archiveBaseName.set("application")
    archiveClassifier.set("")
}
