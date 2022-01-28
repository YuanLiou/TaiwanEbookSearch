import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlinx-serialization")
    id("kotlin-kapt")
}
apply(from = "../gradle/detekt.gradle")

val keystorePath: String by project
val keystoreAlias: String by project
val storePass: String by project
val keyPass: String by project

val ADMOB_ID: String by project
val ADMOB_TEST_DEVICE_ID: String by project
val ADMOB_UNIT_ID: String by project

android {
    compileSdk = AppSettings.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = "liou.rayyuan.ebooksearchtaiwan"
        minSdk = AppSettings.MIN_SDK_VERSION
        targetSdk = AppSettings.MIN_SDK_VERSION
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

    lint {
        abortOnError = false
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
            resValue("string", "package_name", "liou.rayyuan.ebooksearchtaiwan.debug")

            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false // to disable mapping file uploads (default=true if minifying)
            }
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
        jvmTarget = "11"
    }
}

tasks.register("checkVersionCode") {
    println("Version Code is ${getVersionCodeTimeStamps()}")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${AppSettings.DESUGAR_LIB_VERSION}")
    implementation(project(":commonMain"))
    implementation(AppDependencies.CUSTOM_TAB)

    // region Android X Libraries
    AppDependencies.JetPacks.Libs.forEach {
        implementation(it)
    }
    // endregion of Android X Libraries

    // Kotlin
    implementation(AppDependencies.Kotlin.COROUTINE)
    implementation(AppDependencies.Kotlin.SERIALIZATION)

    // Firebase and GMS
    implementation(platform(AppDependencies.Firebase.BOM))
    AppDependencies.Firebase.Libs.forEach {
        implementation(it)
    }
    implementation(AppDependencies.GooglePlayService.ADMOB)

    // Koin
    implementation(AppDependencies.Koin.ANDROID)

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
