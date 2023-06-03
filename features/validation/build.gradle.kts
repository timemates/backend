import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}