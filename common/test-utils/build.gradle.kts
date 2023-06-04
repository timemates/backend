plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.common.authorization)
    implementation(projects.common.validation)
    implementation(projects.core)

    implementation(libs.kotlin.test)
}