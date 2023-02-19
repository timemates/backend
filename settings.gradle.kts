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

//include(":application")
//include(":endpoints")

//include(":integrations:postgresql-storage")
//include(":integrations:localized-time")
//include(":integrations:secure-random-string")
//include(":integrations:smtp-emails")
//include(":integrations:inmemory-repositories")
//include(":integrations:local-files")
//include(":integrations:cache-storage")

//include(":tests:database")
//include(":tests:use-cases")
//
//include("integrations:mongodb-updates")

include(
    ":features:validation",
    ":features:random",
    ":features:authorization",
    ":features:time",
    ":features:page-tokens",
    ":features:scheduler",
)

include(":domain", ":data")

