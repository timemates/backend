import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.google.protobuf)
}

dependencies {
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.google.protobuf)
    implementation(libs.kotlinx.coroutines)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.2"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.54.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}