import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(libs.plugins.conventions.multiplatform.library.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    commonMainImplementation(libs.kotlinx.serialization.json)
    commonMainImplementation(projects.infrastructure.rsocketApi.serializableTypes)
}

mavenPublishing {
    coordinates(
        "io.timemates.api.rsocket",
        "serializable-requests",
        System.getenv("LIB_VERSION") ?: return@mavenPublishing,
    )

    pom {
        name.set("TimeMates RSocket Serializable Requests")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }
}