plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.features.time)
    implementation(projects.features.validation)
    implementation(libs.kotlinx.coroutines)
}

kotlin {
    explicitApi()
}