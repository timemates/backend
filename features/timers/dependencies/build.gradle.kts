plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.features.timers.domain)
    implementation(projects.features.timers.data)
    implementation(projects.foundation.stateMachine)
    implementation(projects.features.timers.adapters)
    implementation(projects.features.users.domain)

    implementation(projects.foundation.random)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.exposed.core)

    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
}