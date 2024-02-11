plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(libs.simple.java.mail)
}