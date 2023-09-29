plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(projects.common.authorization)
    implementation(projects.common.validation)
    implementation(projects.core)

    implementation(libs.kotlin.test)
}