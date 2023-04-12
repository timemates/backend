enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.y9vad9.com")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "timemates-backend"

include(
    ":features:validation",
    ":features:random",
    ":features:authorization",
    ":features:time",
    ":features:page-tokens",
    ":features:scheduler",
)

include(":domain", ":data")

include(":infrastructure:proto", ":infrastructure:services")


