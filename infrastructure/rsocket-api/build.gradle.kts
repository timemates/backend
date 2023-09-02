plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.core)

    implementation(libs.rsocket.server)
    implementation(libs.rsocket.server.websockets)

    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
}