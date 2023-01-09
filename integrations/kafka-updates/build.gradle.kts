plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.kafka.clients)
    implementation(libs.kafka.streams)
    implementation(libs.kotlinx.serialization)
    implementation(projects.useCases)
    implementation(libs.kotlinx.coroutines)
}