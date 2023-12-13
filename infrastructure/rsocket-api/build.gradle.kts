plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.timemates.rsproto)
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
    implementation(projects.core)

    implementation(libs.rsocket.server)
    implementation(libs.rsocket.server.websockets)

    api(libs.timemates.rsproto.server)
    api(libs.timemates.rsproto.client)
    api(libs.timemates.rsproto.common)

    implementation(projects.common.coroutinesUtils)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)

    implementation(libs.kotlinx.serialization.protobuf)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
}

rsproto {
    protoSourcePath = "src/main/proto/"
    generationOutputPath = "generated/rsproto/kotlin"

    clientGeneration = true
    serverGeneration = true
}