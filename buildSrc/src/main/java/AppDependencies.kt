object AppDependencies {
    const val CUSTOM_TAB = "com.github.YuanLiou:chrome-custom-tab-helper:1.1.1"
    const val COIL = "io.coil-kt:coil:1.2.2"

    object JetPacks {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.3.0"
        const val CORE = "androidx.core:core-ktx:1.5.0"
        const val FRAGMENT = "androidx.fragment:fragment-ktx:1.3.4"
        const val ACTIVITY = "androidx.activity:activity-ktx:1.2.3"
        const val RECYCELRVIEW = "androidx.recyclerview:recyclerview:1.2.0"
        const val CARDVIEW = "androidx.cardview:cardview:1.0.0"
        const val PREFERENCE = "androidx.preference:preference-ktx:1.1.1"
        const val MATERIAL_DESIGN = "com.google.android.material:material:1.4.0-rc01"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.0.4"
        // ViewModel and LiveData
        private const val lifecycleLibraryVersion = "2.3.1"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleLibraryVersion"
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
            PAGING
        )

        val common = listOf(
            ROOM,
            LIFECYCLE,
            PREFERENCE,
            PAGING
        )
    }

    object Kotlin {
        const val COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0"
        const val SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1"
        private const val ktorVersion = "1.6.0"
        const val KTOR_CLIENT_ANDROID = "io.ktor:ktor-client-android:$ktorVersion"
        const val KTOR_CLIENT_SERIALIZATION = "io.ktor:ktor-client-serialization:$ktorVersion"
        const val KTOR_CLIENT_LOGGING = "io.ktor:ktor-client-logging:$ktorVersion"
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:28.2.0"
        const val CORE = "com.google.firebase:firebase-core"
        const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics"
        val Libs = listOf(
            CORE,
            CRASHLYTICS
        )
    }

    object GooglePlayService {
        const val ADMOB = "com.google.android.gms:play-services-ads-lite:20.2.0"
        const val CORE = "com.google.android.play:core:1.10.0"
        const val CORE_KTX = "com.google.android.play:core-ktx:1.8.1"
    }

    object Koin {
        private const val koin_version = "3.0.2"
        const val ANDROID = "io.insert-koin:koin-android:$koin_version"
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