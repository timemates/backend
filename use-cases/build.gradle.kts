plugins {
    id(Deps.Plugins.Configuration.Kotlin.Jvm)
}

dependencies {
    implementation(project(Deps.Modules.Domain))
    implementation(Deps.Libs.Kotlinx.Coroutines)
    implementation(project(Deps.Modules.Adapters.GoogleAuthIntegration))
    testImplementation(Deps.Libs.JUnit.Kotlin)
}

tasks.withType<Test> {
    useJUnit()
}