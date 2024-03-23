enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.y9vad9.com")
        maven("https://maven.timemates.org/releases")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.y9vad9.com")
        maven("https://maven.timemates.org/releases")
    }
}

rootProject.name = "timemates-backend"

includeBuild("build-conventions")

include(
    ":foundation:validation",
    ":foundation:validation:tests-integration",
    ":foundation:random",
    ":foundation:authorization",
    ":foundation:time",
    ":foundation:exposed-utils",
    ":foundation:state-machine",
    ":foundation:coroutines-utils",
    ":foundation:page-token",
    ":foundation:mailer",
    ":foundation:mailer:smtp",
    ":foundation:mailer:mailersend",
    ":foundation:mailer:logging",
    ":foundation:cli-arguments",
    ":foundation:hashing",
)

include(
    ":infrastructure:rsocket-api",
)

include(":app")

include(
    ":core:types",
    ":core:types:auth-integration",
)

include(
    ":features:auth:domain",
    ":features:auth:data",
    ":features:auth:dependencies",
    ":features:auth:adapters",
)

include(
    ":features:users:domain",
    ":features:users:data",
    ":features:users:dependencies",
)


include(
    ":features:timers:domain",
    ":features:timers:data",
    ":features:timers:dependencies",
    ":features:timers:adapters",
)
