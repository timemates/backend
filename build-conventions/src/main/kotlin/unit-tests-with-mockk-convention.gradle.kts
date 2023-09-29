plugins {
    id("unit-tests-convention")
}

val mockkVersion = "1.13.5"

dependencies {
    implementation("io.mockk:mockk:$mockkVersion")
}