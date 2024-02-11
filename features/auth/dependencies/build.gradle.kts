plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.features.auth.domain)
    implementation(projects.features.auth.data)
    implementation(projects.features.auth.adapters)

    implementation(projects.foundation.random)

    implementation(libs.exposed.core)
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
}