import java.io.File
import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.kotlin)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, localPropertyFileName)))
}

val HOST: String by project
val HOST_STAGING: String = localProperties.getProperty("HOST_STAGING") ?: HOST
val HOST_PORT: String = localProperties.getProperty("HOST_PORT") ?: "80"

android {
    compileSdk = AppSettings.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AppSettings.MIN_SDK_VERSION
        targetSdk = AppSettings.TARGET_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
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

            buildConfigField("String", "HOST_URL", HOST_STAGING)
            buildConfigField("int", "HOST_PORT", HOST_PORT)
        }

        getByName("release") {
            isMinifyEnabled = true
            buildConfigField("String", "HOST_URL", HOST)
            buildConfigField("int", "HOST_PORT", HOST_PORT)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
        jvmTarget = "11"
    }
    namespace = "com.rayliu.commonmain"
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.threetenabp)
    implementation(libs.fuzzywuzzy)

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.logging)

    // JetPacks
    implementation(libs.room.runtime)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.paging.runtime)
    implementation(libs.androidx.dataStore.core)
    ksp(libs.room.compiler)

    // Koin
    implementation(libs.koin.android)
    testImplementation(libs.androidx.test.ext)
}