import io.gitlab.arturbosch.detekt.Detekt
import java.io.File
import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.detekt.get().pluginId)
    id(libs.plugins.ktlintGradle.get().pluginId)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.spotless)
}

val localProperties =
    Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, localPropertyFileName)))
    }

val HOST: String by project
val hostStaging: String = localProperties.getProperty("HOST_STAGING") ?: HOST
val hostPort: String = localProperties.getProperty("HOST_PORT") ?: "80"

android {
    compileSdk = AppSettings.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AppSettings.MIN_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    flavorDimensions.add("data_source")
    productFlavors {
        create("api") {
            dimension = "data_source"
        }
        create("mock") {
            dimension = "data_source"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false

            buildConfigField("String", "HOST_URL", hostStaging)
            buildConfigField("int", "HOST_PORT", hostPort)
        }

        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "HOST_URL", HOST)
            buildConfigField("int", "HOST_PORT", hostPort)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    androidComponents {
        beforeVariants(
            selector()
                .withFlavor(Pair("data_source", "mock"))
                .withBuildType("release")
        ) { variantBuilder ->
            variantBuilder.enable = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        freeCompilerArgs =
            listOf(
                "-Xjvm-default=all",
                "-Xstring-concat=inline"
            )
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }

    namespace = "com.rayliu.commonmain"
}

sqldelight {
    databases {
        create("EbookTwDatabase") {
            packageName.set("com.rayliu.commonmain.data.database")
            verifyMigrations.set(true)
        }
    }
}

detekt {
    toolVersion = libs.versions.detektVersion.toString()
    config.setFrom(files("$project.rootDir/deteket-config.yml"))
    buildUponDefaultConfig = true
    parallel = true
}

spotless {
    val ktlintVersion = libs.versions.ktlintCli.get()

    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        ktlint(ktlintVersion).setEditorConfigPath(rootProject.file(".editorconfig").path)
        toggleOffOn()
        trimTrailingWhitespace()
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint(ktlintVersion)
    }
}

tasks.register<Detekt>("detektAll") {
    description = "Runs Detekt on the whole project at once."
    parallel = true
    setSource(projectDir)
    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")
    config.setFrom(project.file("../deteket-config.yml"))
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.fuzzywuzzy)

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.logging)

    // JetPacks
    implementation(libs.androidx.preference.ktx)
    implementation(libs.paging.runtime)
    implementation(libs.androidx.dataStore.core)

    // Koin
    val koinBom = platform(libs.koin.bom)
    implementation(koinBom)
    implementation(libs.koin.android)
    testImplementation(libs.androidx.test.ext)

    // SQL Delight
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.sqldelight.paging3.extensions)

    // Detekt
    detekt(libs.detekt.cli)
    detektPlugins(libs.detekt.ktlint.formatting)
}
