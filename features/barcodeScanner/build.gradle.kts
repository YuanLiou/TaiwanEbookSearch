@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.dynamic-feature")
    id("kotlin-android")
    alias(libs.plugins.compose.compiler)
    id(libs.plugins.ktlintGradle.get().pluginId)
}

android {
    namespace = AppSettings.APPLICATION_ID + ".camerapreview"
    compileSdk = AppSettings.COMPILE_SDK_VERSION
    flavorDimensions.add("data_source")
    productFlavors {
        create("api") {
            dimension = "data_source"
        }
        create("mock") {
            dimension = "data_source"
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

    defaultConfig {
        minSdk = AppSettings.MIN_SDK_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    buildTypes {
        getByName("release") {
            proguardFiles("proguard-rules-barcode-scanner.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":app"))
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.activity)
    implementation(libs.compose.lifecycle)
    implementation(libs.compose.livedata)
    implementation(libs.constraintlayout.compose)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.navigation)
    implementation(libs.request.permission.compose)

    // camera related libs
    implementation(libs.viewfinder.compose)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.guava.android)

    // Koin
    val koinBom = platform(libs.koin.bom)
    implementation(koinBom)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
