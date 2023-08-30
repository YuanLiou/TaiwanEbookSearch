import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.io.File
import java.io.FileInputStream
import java.util.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlinx-serialization")
}
apply(from = "../gradle/detekt.gradle")

val localProperties = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, localPropertyFileName)))
}

val keystorePath: String = localProperties.getProperty("keystorePath")
val keystoreAlias: String = localProperties.getProperty("keystoreAlias")
val storePass: String = localProperties.getProperty("storePass")
val keyPass: String = localProperties.getProperty("keyPass")

val ADMOB_ID: String = localProperties.getProperty("ADMOB_ID")
val ADMOB_TEST_DEVICE_ID: String = localProperties.getProperty("ADMOB_TEST_DEVICE_ID")
val ADMOB_UNIT_ID: String = localProperties.getProperty("ADMOB_UNIT_ID")

android {
    compileSdk = AppSettings.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = "liou.rayyuan.ebooksearchtaiwan"
        minSdk = AppSettings.MIN_SDK_VERSION
        targetSdk = AppSettings.TARGET_SDK_VERSION
        versionCode = AppSettings.VERSION_CODE
        versionName = rootProject.extra.get("app_version").toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("en", "zh-rTW", "zh-rCN")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(mapOf("room.schemaLocation" to "$projectDir/schemas", "room.incremental" to "true"))
            }
        }
    }

    buildFeatures {
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

    androidComponents {
        beforeVariants(
            selector()
                .withFlavor(Pair("data_source", "mock"))
                .withBuildType("release")
        ) { variantBuilder ->
            variantBuilder.enable = false
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

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${AppSettings.DESUGAR_LIB_VERSION}")
    implementation(project(":commonMain"))
    implementation(AppDependencies.CUSTOM_TAB)
    implementation(AppDependencies.THREE_TEN)

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
    implementation(AppDependencies.GooglePlayService.IN_APP_REVIEW)

    // Koin
    implementation(AppDependencies.Koin.ANDROID)

    // Zxing
    implementation(AppDependencies.Zxing.ZXING_ANDROID)

    // Coil
    implementation(AppDependencies.COIL)
    testImplementation(AppDependencies.Test.JUNIT)
    androidTestImplementation(AppDependencies.Test.CORE)
    androidTestImplementation(AppDependencies.Test.RUNNER)
    androidTestImplementation(AppDependencies.Test.ESPRESSO)
}
