plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    api(projects.foundation.time)
    api(projects.foundation.validation)
    api(projects.foundation.authorization)

    implementation(projects.foundation.stateMachine)
}