enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
    ":common:validation",
    ":common:random",
    ":common:authorization",
    ":common:time",
    ":common:scheduler",
    ":common:exposed-utils",
    ":common:test-utils",
    ":common:state-machine",
    ":common:coroutines-utils",
    ":common:page-token",
    ":common:smtp-mailer",
    ":common:cli-arguments",
)

include(":core", ":data")

include(":infrastructure:grpc-api")

include(":app")


