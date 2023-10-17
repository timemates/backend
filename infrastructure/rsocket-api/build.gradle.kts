plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.core)
    implementation(projects.infrastructure.rsocketApi.serializableRequests)
    implementation(projects.infrastructure.rsocketApi.serializableTypes)
    implementation(projects.core.serializableAdapter)

    implementation(libs.rsocket.server)
    implementation(libs.rsocket.server.websockets)

    implementation(projects.common.coroutinesUtils)

    implementation(libs.y9vad9.rsocket.router)
    implementation(libs.y9vad9.rsocket.router.serialization.core)
    implementation(libs.y9vad9.rsocket.router.serialization.json)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.y9vad9.rsocket.router.versioning.core)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)

    testImplementation(libs.y9vad9.rsocket.router.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
}