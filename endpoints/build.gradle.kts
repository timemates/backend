plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(libs.ktor.server.core)
    api(libs.ktor.json)
    api(libs.ktor.server.websockets)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
}