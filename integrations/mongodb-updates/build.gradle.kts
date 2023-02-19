plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization)
    implementation(libs.litote.kmongo.async)
    implementation(libs.litote.kmongo.coroutines)
}