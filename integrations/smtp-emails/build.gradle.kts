plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.simple.java.mail)
    implementation(projects.useCases)
}