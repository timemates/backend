plugins {
    id(Deps.Plugins.Configuration.Kotlin.Jvm)
    id(Deps.Plugins.Serialization.Id)
}

dependencies {
    implementation(Deps.Libs.Ktor.Client.Core)
    implementation(Deps.Libs.Ktor.Json)
    implementation(Deps.Libs.Ktor.Client.ContentNegotiation)
    implementation(Deps.Libs.Ktor.Client.Cio)
}