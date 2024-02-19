import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}

dependencies {
    implementation(libs.kotlin.test)
    implementation(projects.foundation.validation)
}