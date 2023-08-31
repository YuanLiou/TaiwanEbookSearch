import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.io.File
import java.io.FileInputStream
import java.util.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.serialization)
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
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(project(":commonMain"))
    implementation(libs.custom.tab)
    implementation(libs.threetenabp)

    // region Android X Libraries
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.livedata.java8)
    implementation(libs.paging.runtime)
    // endregion of Android X Libraries

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Firebase and GMS
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.admob)
    implementation(libs.play.review.ktx)

    // Koin
    implementation(libs.koin.android)

    // Zxing
    implementation(libs.zxing.android)

    // Coil
    implementation(libs.coil.kt)
    testImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
