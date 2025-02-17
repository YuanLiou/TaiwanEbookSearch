// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlintGradle) apply false
    alias(libs.plugins.compose.compiler) apply false
    // Add the dependency for the App Distribution Gradle plugin
    id("com.google.firebase.appdistribution") version "5.1.1" apply false
}

buildscript {
    val isUseUnstableBuildTool = (project.properties["useUnstableGradleBuildTool"] as? String)?.toBoolean()
        ?: false
    println("is use unstable build tool = $isUseUnstableBuildTool")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://jitpack.io")
    }
}

val app_version by extra(AppSettings.VERSION_NAME)

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks {
    val clean by registering(Delete::class) {
        println("Start cleaning... build Dir")
        delete(rootProject.layout.buildDirectory)
        println("Clean finished")
    }
}
