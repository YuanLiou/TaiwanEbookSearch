// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val detekt_version by extra("1.0.0.RC9.2")

    val isUseUnstableBuildTool = (project.properties["useUnstableGradleBuildTool"] as? String)?.toBoolean()
        ?: false
    println("is use unstable build tool = $isUseUnstableBuildTool")

    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${AppSettings.AGP_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${AppSettings.KOTLIN_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${AppSettings.KOTLIN_VERSION}")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.4")
        classpath("gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detekt_version")
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
        delete(rootProject.buildDir)
        println("Clean finished")
    }
}
