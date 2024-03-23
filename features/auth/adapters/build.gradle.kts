plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.features.auth.domain)
    implementation(projects.features.users.domain)

    implementation(projects.foundation.random)
    implementation(projects.foundation.mailer.smtp)
    implementation(projects.foundation.mailer.mailersend)
    implementation(projects.foundation.mailer.logging)
}