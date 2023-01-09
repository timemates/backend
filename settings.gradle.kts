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

rootProject.name = "timemates-backend"

include(":use-cases")
include(":application")

include(":integrations:postgresql-repositories")
include(":integrations:localized-time")
include(":integrations:secure-random-string")
include(":integrations:kafka-updates")
include(":integrations:smtp-emails")
include(":integrations:inmemory-repositories")

include(":tests:database")
include(":tests:use-cases")