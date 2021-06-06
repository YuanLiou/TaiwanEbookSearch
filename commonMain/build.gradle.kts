plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
    id("kotlin-kapt")
}

val HOST_STAGING: String by project
val HOST: String by project

android {
    compileSdkVersion(AppSettings.COMPILE_SDK_VERSION)

    defaultConfig {
        minSdkVersion(AppSettings.MIN_SDK_VERSION)
        targetSdkVersion(AppSettings.TARGET_SDK_VERSION)
        versionCode = getVersionCodeTimeStamps()
        versionName = rootProject.extra.get("app_version").toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false

            buildConfigField("String", "HOST_URL", HOST_STAGING)
        }

        getByName("release") {
            isMinifyEnabled = true
            isZipAlignEnabled = true
            buildConfigField("String", "HOST_URL", HOST)
            consumerProguardFiles("consumer-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
        jvmTarget = "1.8"
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${AppSettings.DESUGAR_LIB_VERSION}")
    implementation(AppDependencies.CUSTOM_TAB)

    // Kotlin
    implementation(AppDependencies.Kotlin.COROUTINE)
    implementation(AppDependencies.Kotlin.SERIALIZATION)
    implementation(AppDependencies.Kotlin.KTOR_CLIENT_ANDROID)
    implementation(AppDependencies.Kotlin.KTOR_CLIENT_SERIALIZATION)
    implementation(AppDependencies.Kotlin.KTOR_CLIENT_LOGGING)

    // JetPacks
    AppDependencies.JetPacks.common.forEach {
        implementation(it)
    }
    kapt(AppDependencies.JetPacks.ROOM_COMPILER)

    // Koin
    implementation(AppDependencies.Koin.ANDROID)

    testImplementation(AppDependencies.Test.JUNIT)
    androidTestImplementation(AppDependencies.Test.RUNNER)
    androidTestImplementation(AppDependencies.Test.ESPRESSO)
}