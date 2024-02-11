plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.features.timers.domain)
    implementation(projects.features.timers.data)

    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
}