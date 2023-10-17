import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

mavenPublishing {
    coordinates(
        "io.timemates.api.rsocket",
        "serializable-types",
        System.getenv("LIB_VERSION") ?: return@mavenPublishing,
    )

    pom {
        name.set("TimeMates RSocket Serializable Types")
    }
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
    }
}

dependencies {
    commonMainImplementation(libs.kotlinx.serialization.json)
}