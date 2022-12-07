pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    plugins {
        kotlin("plugin.serialization") version "1.7.20"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.y9vad9.com")
    }
}

rootProject.name = "tomadoro-backend"

includeBuild("build-logic/dependencies")
includeBuild("build-logic/configuration")
includeBuild("build-logic/service-deploy")
//includeBuild("buildUtils/library-deploy")

include(":domain")
include(":use-cases")
include(":application")
include(":adapters:codes-integration")
include(":adapters:google-auth-integration")
include(":adapters:repositories-integration")
include(":adapters:time-integration")
include(":adapters:tokens-integration")
