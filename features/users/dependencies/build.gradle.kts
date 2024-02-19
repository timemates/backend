plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.features.users.domain)
    implementation(projects.features.users.data)

    implementation(libs.exposed.core)
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
}