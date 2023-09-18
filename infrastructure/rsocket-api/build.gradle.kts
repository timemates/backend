plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.core)
    implementation(projects.core.serializableTypes)

    implementation(libs.rsocket.server)
    implementation(libs.rsocket.server.websockets)

    implementation(projects.common.coroutinesUtils)

    implementation(libs.y9vad9.rsocket.router)

    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
}