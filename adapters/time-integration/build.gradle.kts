plugins {
    id(Deps.Plugins.Configuration.Kotlin.Jvm)
}

dependencies {
    implementation(project(Deps.Modules.UseCases))
    implementation(project(Deps.Modules.Domain))
}