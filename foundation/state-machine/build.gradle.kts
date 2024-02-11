plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(projects.foundation.time)
    implementation(projects.foundation.validation)
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
}

kotlin {
    explicitApi()
}