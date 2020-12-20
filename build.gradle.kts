// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val detekt_version by extra("1.0.0.RC9.2")

    val isUseUnstableBuildTool = (project.properties["useUnstableGradleBuildTool"] as? String)?.toBoolean()
        ?: false
    println("is use unstable build tool = $isUseUnstableBuildTool")

    repositories {
        google()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${AppSettings.AGP_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${AppSettings.KOTLIN_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${AppSettings.KOTLIN_VERSION}")
        classpath("com.google.gms:google-services:4.3.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.3.0")
        classpath("gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detekt_version")
    }
}

val app_version by extra("1.11")

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks {
    val clean by registering(Delete::class) {
        println("Start cleaning... build Dir")
        delete(rootProject.buildDir)
        println("Clean finished")
    }
}
