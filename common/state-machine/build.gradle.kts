plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(projects.common.time)
    implementation(projects.common.validation)
    implementation(libs.kotlinx.coroutines)
}

kotlin {
    explicitApi()
}