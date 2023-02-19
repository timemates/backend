import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}

dependencies {
    implementation(projects.features.validation)
}