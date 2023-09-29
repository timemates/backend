plugins {
    id("jvm-module-convention")
}

val kotlinVersion = KotlinVersion.CURRENT.toString()
val coroutinesVersion = "1.7.1"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    implementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}