object AppDependencies {
    const val CUSTOM_TAB = "com.github.YuanLiou:chrome-custom-tab-helper:1.1.1"
    const val THREE_TEN_ABP = "com.jakewharton.threetenabp:threetenabp:1.2.1"
    const val COIL = "io.coil-kt:coil:1.1.0"

    object JetPacks {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.2.0"
        const val CORE = "androidx.core:core-ktx:1.3.2"
        const val FRAGMENT = "androidx.fragment:fragment-ktx:1.2.5"
        const val ACTIVITY = "androidx.activity:activity-ktx:1.1.0"
        const val RECYCELRVIEW = "androidx.recyclerview:recyclerview:1.1.0"
        const val CARDVIEW = "androidx.cardview:cardview:1.0.0"
        const val PREFERENCE = "androidx.preference:preference-ktx:1.1.1"
        const val MATERIAL_DESIGN = "com.google.android.material:material:1.2.1"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.0.4"
        // ViewModel and LiveData
        private const val lifecycleLibraryVersion = "2.2.0"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-extensions:$lifecycleLibraryVersion"
        // Java8 support for Lifecycles
        const val LIFECYCLE_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$lifecycleLibraryVersion"

        // Room
        private const val roomVersion = "2.2.5"
        const val ROOM = "androidx.room:room-runtime:$roomVersion"
        const val ROOM_COMPILER = "androidx.room:room-compiler:$roomVersion"
        const val PAGING = "androidx.paging:paging-runtime:2.1.2"
        val Libs = listOf(
            APPCOMPAT,
            CORE,
            FRAGMENT,
            ACTIVITY,
            RECYCELRVIEW,
            CARDVIEW,
            PREFERENCE,
            MATERIAL_DESIGN,
            CONSTRAINT_LAYOUT,
            LIFECYCLE,
            LIFECYCLE_JAVA8,
            ROOM,
            PAGING
        )
    }

    object Kotlin {
        const val COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0"
        const val SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1"
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:25.4.0"
        const val CORE = "com.google.firebase:firebase-core"
        const val ADS = "com.google.firebase:firebase-ads-lite"
        const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics"
        val Libs = listOf(
            CORE,
            ADS,
            CRASHLYTICS
        )
    }

    object Retrofit {
        private const val retrofit_version = "2.9.0"
        const val RETROFIT = "com.squareup.retrofit2:retrofit:$retrofit_version"
        const val PROFILER = "com.localebro:okhttpprofiler:1.0.8"
        const val SERIALIZATION = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
        const val OKHTTP = "com.squareup.okhttp3:okhttp:4.9.1"
        val Libs = listOf(
            RETROFIT,
            PROFILER,
            SERIALIZATION,
            OKHTTP
        )
    }

    object Koin {
        private const val koin_version = "3.0.1"
        const val KOIN = "io.insert-koin:koin-android:$koin_version"
        const val KOIN_VIEWMODEL = "io.insert-koin:koin-android:$koin_version"
    }

    object Zxing {
        const val ZXING_CORE = "com.google.zxing:core:3.3.0"
        const val ZXING_ANDROID = "com.journeyapps:zxing-android-embedded:4.1.0"
    }

    object Test {
        const val JUNIT = "junit:junit:4.12"
        const val RUNNER = "androidx.test:runner:1.1.0"
        const val ESPRESSO = "androidx.test.espresso:espresso-core:3.1.0"
    }
}