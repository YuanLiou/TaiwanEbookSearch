// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version by extra("1.4.10")
    val detekt_version by extra("1.0.0.RC9.2")

    val isUseUnstableBuildTool = (project.properties["useUnstableGradleBuildTool"] as? String)?.toBoolean()
        ?: false
    println("is use unstable build tool = $isUseUnstableBuildTool")

    repositories {
        google()
        jcenter()

        maven(url = "https://maven.fabric.io/public")
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("com.google.gms:google-services:4.3.2")
        classpath("io.fabric.tools:gradle:1.31.0")
        classpath("gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detekt_version")
    }
}

val app_version by extra("1.10")

val androidx_version by extra("1.0.0")
val retrofit_version by extra("2.6.2")
val fresco_version by extra("2.0.0")
val koin_version by extra("2.2.0-beta-1")
val room_version by extra("2.2.5")

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
