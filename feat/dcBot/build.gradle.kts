plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":core"))

    implementation(libs.napier)

    api(libs.koin.core)
}