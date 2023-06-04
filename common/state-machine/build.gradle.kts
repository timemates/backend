plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.common.time)
    implementation(projects.common.validation)
    implementation(libs.kotlinx.coroutines)
}

kotlin {
    explicitApi()
}