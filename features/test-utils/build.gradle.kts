plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.features.authorization)
    implementation(projects.features.validation)
    implementation(projects.domain)

    implementation(libs.kotlin.test)
}