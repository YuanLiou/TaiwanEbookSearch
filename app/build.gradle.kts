import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    id("kotlinx-serialization")
    id("kotlin-kapt")
}
apply(from = "../gradle/detekt.gradle")

val keystorePath: String by project
val keystoreAlias: String by project
val storePass: String by project
val keyPass: String by project

val HOST_STAGING: String by project
val HOST: String by project

val ADMOB_ID: String by project
val ADMOB_TEST_DEVICE_ID: String by project
val ADMOB_UNIT_ID: String by project

android {
    compileSdkVersion(AppSettings.COMPILE_SDK_VERSION)

    defaultConfig {
        applicationId = "liou.rayyuan.ebooksearchtaiwan"
        minSdkVersion(AppSettings.MIN_SDK_VERSION)
        targetSdkVersion(AppSettings.TARGET_SDK_VERSION)
        versionCode = getVersionCodeTimeStamps()
        versionName = rootProject.extra.get("app_version").toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(mapOf("room.schemaLocation" to "$projectDir/schemas", "room.incremental" to "true"))
            }
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    lintOptions {
        isAbortOnError = false
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystorePath)
            keyAlias = keystoreAlias
            storePassword = storePass
            keyPassword = keyPass
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            buildConfigField("String", "HOST_URL", HOST_STAGING)
            resValue("string", "package_name", "liou.rayyuan.ebooksearchtaiwan.debug")
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
            isZipAlignEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("String", "HOST_URL", HOST)
            resValue("string", "package_name", "liou.rayyuan.ebooksearchtaiwan")
        }
    }

    buildTypes.all {
        resValue("string", "AD_MOB_ID", ADMOB_ID)
        buildConfigField("String", "ADMOB_TEST_DEVICE_ID", ADMOB_TEST_DEVICE_ID)
        resValue("string", "AD_MOB_UNIT_ID", ADMOB_UNIT_ID)
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

fun getVersionCodeTimeStamps(): Int {
    val date = Date()
    val dateFormatter = SimpleDateFormat("yyMMddHH")
    val formattedDate = "19" + dateFormatter.format(date)
    return formattedDate.toInt()
}

tasks.register("checkVersionCode") {
    println("Version Code is ${getVersionCodeTimeStamps()}")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${AppSettings.DESUGAR_LIB_VERSION}")
    implementation(AppDependencies.CUSTOM_TAB)

    // region Android X Libraries
    AppDependencies.JetPacks.Libs.forEach {
        implementation(it)
    }
    kapt(AppDependencies.JetPacks.ROOM_COMPILER)
    // endregion of Android X Libraries

    // Kotlin
    implementation(AppDependencies.Kotlin.COROUTINE)
    implementation(AppDependencies.Kotlin.SERIALIZATION)
    implementation(AppDependencies.Kotlin.KTOR_CLIENT_ANDROID)
    implementation(AppDependencies.Kotlin.KTOR_CLIENT_SERIALIZATION)
    implementation(AppDependencies.Kotlin.KTOR_CLIENT_LOGGING)

    // Firebase and GMS
    implementation(platform(AppDependencies.Firebase.BOM))
    AppDependencies.Firebase.Libs.forEach {
        implementation(it)
    }

    // Koin
    implementation(AppDependencies.Koin.KOIN)

    // Zxing
    implementation(AppDependencies.Zxing.ZXING_ANDROID) {
        isTransitive = false
    }
    implementation(AppDependencies.Zxing.ZXING_CORE)

    // Coil
    implementation(AppDependencies.COIL)
    testImplementation(AppDependencies.Test.JUNIT)
    androidTestImplementation(AppDependencies.Test.RUNNER)
    androidTestImplementation(AppDependencies.Test.ESPRESSO)
}

// Firebase config needs to put on bottom
apply(plugin = "com.google.gms.google-services")
