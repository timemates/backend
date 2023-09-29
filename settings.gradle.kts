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

includeBuild("build-conventions")

include(
    ":common:validation",
    ":common:random",
    ":common:authorization",
    ":common:time",
    ":common:exposed-utils",
    ":common:test-utils",
    ":common:state-machine",
    ":common:coroutines-utils",
    ":common:page-token",
    ":common:smtp-mailer",
    ":common:cli-arguments",
    ":common:hashing",
)

include(":core", ":core:serializable-types")

include(":data")

include(
    ":infrastructure:grpc-api",
    ":infrastructure:rsocket-api",
)

include(":app")
