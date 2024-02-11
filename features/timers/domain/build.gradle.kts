plugins {
    id(libs.plugins.jvm.module.convention.get().pluginId)
}

dependencies {
    implementation(projects.foundation.time)
    implementation(projects.foundation.pageToken)
    implementation(projects.foundation.random)

    implementation(libs.kotlinx.coroutines)
    implementation(projects.core.types.authIntegration)
    api(projects.core.types)


    implementation(projects.foundation.stateMachine)
}

tasks.withType<Test> {
    useJUnitPlatform()
}