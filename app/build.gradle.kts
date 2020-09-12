import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("io.fabric")
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
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "liou.rayyuan.ebooksearchtaiwan"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = getVersionCodeTimeStamps()
        versionName = rootProject.extra.get("app_version").toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf("room.schemaLocation" to "$projectDir/schemas", "room.incremental" to "true")
            }
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
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
            setShrinkResources(true)
            isZipAlignEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("String", "HOST_URL", HOST)
            resValue("string", "package_name", "liou.rayyuan.ebooksearchtaiwan")
        }
    }

    buildTypes.all {
        buildConfigField("String", "AD_MOB_ID", ADMOB_ID)
        buildConfigField("String", "ADMOB_TEST_DEVICE_ID", ADMOB_TEST_DEVICE_ID)
        resValue("string", "AD_MOB_UNIT_ID", ADMOB_UNIT_ID)
    }

    compileOptions {
        coreLibraryDesugaringEnabled = true
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

androidExtensions {
    isExperimental = true
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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")
    implementation("liou.rayyuan.chromecustomtabhelper:chrome-custom-tab-helper:1.1.2")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

    // region Android X Libraries
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.fragment:fragment-ktx:1.2.5")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("com.google.android.material:material:1.2.1")
    // ViewModel and LiveData
    val lifecycleLibraryVersion = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleLibraryVersion")
    // Java8 support for Lifecycles
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleLibraryVersion")

    // Room
    val roomVersion = rootProject.extra.get("room_version")
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.paging:paging-runtime:2.1.2")
    // endregion of Android X Libraries

    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")

    // Fresco
    val fresco_version = rootProject.extra.get("fresco_version")
    implementation("com.facebook.fresco:fresco:$fresco_version")
    implementation("com.facebook.fresco:imagepipeline-okhttp3:$fresco_version")

    // Firebase
    implementation("com.google.firebase:firebase-core:17.2.0")
    implementation("com.google.firebase:firebase-ads:18.2.0")
    implementation("com.google.firebase:firebase-ml-vision:23.0.0")
    implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")

    // Retrofit 2
    val retrofit_version = rootProject.extra.get("retrofit_version")
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.itkacher.okhttpprofiler:okhttpprofiler:1.0.7")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.7.0")

    // Koin
    val koin_version = rootProject.extra.get("koin_version")
    implementation("org.koin:koin-android:$koin_version")
    implementation("org.koin:koin-androidx-viewmodel:$koin_version")

    // disable for Google Play instant App testing
    debugImplementation("com.amitshekhar.android:debug-db:1.0.6")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0")
}

// Firebase config needs to put on bottom
apply(plugin = "com.google.gms.google-services")
