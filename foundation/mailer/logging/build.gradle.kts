plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    api(projects.foundation.mailer)
}