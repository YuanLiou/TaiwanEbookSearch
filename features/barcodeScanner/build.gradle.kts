@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.dynamic-feature")
    id("kotlin-android")
    alias(libs.plugins.compose.compiler)
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
}
