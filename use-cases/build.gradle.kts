plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {

    implementation(libs.kotlinx.coroutines)
    testImplementation(libs.junit.kotlin)
}

tasks.withType<Test> {
    useJUnit()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}