// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version by extra("1.3.31")
    val detekt_version by extra("1.0.0.RC9.2")
    repositories {
        google()
        jcenter()

        maven(url = "https://maven.fabric.io/public")
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.gms:google-services:4.2.0")
        classpath("io.fabric.tools:gradle:1.28.1")
        classpath("gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detekt_version")
    }
}

val app_version by extra("1.9")

val androidx_version by extra("1.0.0")
val retrofit_version by extra("2.5.0")
val fresco_version by extra("1.13.0")
val koin_version by extra("2.0.0-rc-2")
val room_version by extra("2.0.0-beta01")

allprojects {
    repositories {
        google()
        jcenter()
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
