import com.google.devtools.ksp.gradle.KspTask
import com.google.devtools.ksp.gradle.KspTaskJvm

plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.timemates.rsproto)
    alias(libs.plugins.ksp)
}

sourceSets {
    getByName("main") {
        kotlin.srcDirs(
            "src/main/kotlin",
            "build/generated/rsproto/kotlin",
        )
    }
}

dependencies {
    implementation(projects.features.auth.domain)
    implementation(projects.features.users.domain)
    implementation(projects.features.timers.domain)
    implementation(projects.foundation.pageToken)

    implementation(libs.rsocket.server)
    implementation(libs.rsocket.server.websockets)

    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    api(libs.timemates.rsproto.server)
    api(libs.timemates.rsproto.client)
    api(libs.timemates.rsproto.common)

    implementation(projects.foundation.coroutinesUtils)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)

    implementation(libs.kotlinx.serialization.protobuf)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
}

rsproto {
    protoSourcePath.set("src/main/proto")
    generationOutputPath.set("generated/rsproto/kotlin")

    clientGeneration.set(true)
    serverGeneration.set(true)
}

tasks.withType<KspTaskJvm> {
    inputs.files(tasks.named("generateProto").get().outputs.files)
}