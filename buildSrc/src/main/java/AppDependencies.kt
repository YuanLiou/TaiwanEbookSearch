object AppDependencies {
    const val CUSTOM_TAB = "com.github.YuanLiou:chrome-custom-tab-helper:1.1.1"
    const val COIL = "io.coil-kt:coil:1.4.0"

    object JetPacks {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.6.1"
        const val CORE = "androidx.core:core-ktx:1.9.0"
        const val FRAGMENT = "androidx.fragment:fragment-ktx:1.5.5"
        const val ACTIVITY = "androidx.activity:activity-ktx:1.6.1"
        const val RECYCELRVIEW = "androidx.recyclerview:recyclerview:1.2.1"
        const val CARDVIEW = "androidx.cardview:cardview:1.0.0"
        const val PREFERENCE = "androidx.preference:preference-ktx:1.2.0"
        const val MATERIAL_DESIGN = "com.google.android.material:material:1.4.0-rc01"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val DATASTORE_PREFERENCE = "androidx.datastore:datastore-preferences:1.0.0"
        // ViewModel and LiveData
        private const val lifecycleLibraryVersion = "2.5.1"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleLibraryVersion"
        // Java8 support for Lifecycles
        const val LIFECYCLE_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$lifecycleLibraryVersion"

        // Room
        private const val roomVersion = "2.5.0"
        const val ROOM = "androidx.room:room-runtime:$roomVersion"
        const val ROOM_COMPILER = "androidx.room:room-compiler:$roomVersion"
        const val PAGING = "androidx.paging:paging-runtime:3.1.1"
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
            PAGING,
            DATASTORE_PREFERENCE
        )
    }

    object Kotlin {
        const val COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        const val SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1"
        private const val ktorVersion = "2.2.3"
        const val KTOR_CLIENT_ANDROID = "io.ktor:ktor-client-android:$ktorVersion"
        const val KTOR_CLIENT_CONTENT_NEGOTIATION = "io.ktor:ktor-client-content-negotiation:$ktorVersion"
        const val KTOR_CLIENT_SERIALIZATION = "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion"
        const val KTOR_CLIENT_LOGGING = "io.ktor:ktor-client-logging:$ktorVersion"
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:31.2.2"
        const val ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
        const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics-ktx"
        val Libs = listOf(
            ANALYTICS,
            CRASHLYTICS
        )
    }

    object GooglePlayService {
        const val ADMOB = "com.google.android.gms:play-services-ads-lite:21.5.0"
    }

    object Koin {
        private const val koin_version = "3.1.5"
        const val ANDROID = "io.insert-koin:koin-android:$koin_version"
    }

    object Zxing {
        const val ZXING_CORE = "com.google.zxing:core:3.3.0"
        const val ZXING_ANDROID = "com.journeyapps:zxing-android-embedded:4.1.0"
    }

    object Test {
        const val JUNIT = "junit:junit:4.12"
        const val RUNNER = "androidx.test:runner:1.4.0"
        const val CORE = "androidx.test:core:1.4.0"
        const val ESPRESSO = "androidx.test.espresso:espresso-core:3.1.0"
    }
}