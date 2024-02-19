plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(projects.core.types)
    implementation(projects.foundation.authorization)
}