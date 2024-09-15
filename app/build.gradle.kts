import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import io.gitlab.arturbosch.detekt.Detekt
import java.io.File
import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.compose.compiler)
    id(libs.plugins.detekt.get().pluginId)
    id(libs.plugins.ktlintGradle.get().pluginId)
}

val localProperties =
    Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, localPropertyFileName)))
    }

val keystorePath: String = localProperties.getProperty("keystorePath")
val keystoreAlias: String = localProperties.getProperty("keystoreAlias")
val storePass: String = localProperties.getProperty("storePass")
val keyPass: String = localProperties.getProperty("keyPass")

val admobId: String = localProperties.getProperty("ADMOB_ID")
val admobTestDeviceId: String = localProperties.getProperty("ADMOB_TEST_DEVICE_ID")
val admobUnitId: String = localProperties.getProperty("ADMOB_UNIT_ID")

android {
    compileSdk = AppSettings.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = AppSettings.APPLICATION_ID
        minSdk = AppSettings.MIN_SDK_VERSION
        targetSdk = AppSettings.TARGET_SDK_VERSION
        versionCode = AppSettings.VERSION_CODE
        versionName = rootProject.extra.get("app_version").toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("en", "zh-rTW", "zh-rCN")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
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
        resValue("string", "AD_MOB_ID", admobId)
        buildConfigField("String", "ADMOB_TEST_DEVICE_ID", admobTestDeviceId)
        resValue("string", "AD_MOB_UNIT_ID", admobUnitId)
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
    namespace = "liou.rayyuan.ebooksearchtaiwan"
}

detekt {
    toolVersion = libs.versions.detektVersion.toString()
    config.setFrom(files("$project.rootDir/deteket-config.yml"))
    buildUponDefaultConfig = true
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(project(":commonMain"))

    // region Compose
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.activity)
    implementation(libs.compose.lifecycle)
    implementation(libs.compose.livedata)
    implementation(libs.constraintlayout.compose)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.compose.navigation)
    implementation(libs.viewfinder.compose)
    implementation(libs.request.permission.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.compose.ui.test.junit4)
    // endregion Compose

    // region Android X Libraries
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.constraintlayout)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.livedata.java8)
    implementation(libs.paging.runtime)
    // endregion of Android X Libraries

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections.immutable)

    // Firebase and GMS
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.admob)
    implementation(libs.play.review.ktx)

    // Koin
    val koinBom = platform(libs.koin.bom)
    implementation(koinBom)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Coil
    implementation(libs.coil.kt)
    testImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // camera related libs
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.guava.android)
    implementation(libs.barcode.scanning)

    // Detekt
    detekt(libs.detekt.cli)
    detektPlugins(libs.detekt.ktlint.formatting)
}
