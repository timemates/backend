plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization)
}

kotlin {
    explicitApi()
}