<build>
- If you want to validating or build to testing, use following command:
```sh
./gradlew assembleMockDebug
```
or execute this Gradle Task: `assembleMockDebug`

</build>

<project_context>
# AI Project Context - Taiwan Ebook Search

## 1. Project Overview

This is an Android application that enables users to search for ebooks across multiple Taiwan ebook stores simultaneously. The app is built with Kotlin and uses Jetpack Compose for the UI, following a clean architecture pattern with shared business logic in a common module.

## 2. Tech Stack

* **Languages:** Kotlin
* **Main Framework/UI:** Android (Jetpack Compose with Material 3), Clean Architecture (MVVM)
* **Key Dependencies:**
  * **UI:** Jetpack Compose (Material 3, Navigation, Adaptive Layout), Coil (image loading)
  * **Networking:** Ktor (HTTP client with JSON serialization)
  * **Database:** SQLDelight (type-safe SQL queries)
  * **Async:** Kotlin Coroutines
  * **Dependency Injection:** Koin
  * **Architecture:** Paging 3, DataStore (preferences)
  * **Firebase:** Analytics, Crashlytics, App Distribution
  * **Ads:** AdMob
  * **Camera:** CameraX (barcode scanning)
  * **Code Quality:** Detekt, Ktlint, Spotless
* **Package Management:** Gradle (KTS - Kotlin DSL)
* **Platforms:** Android (with product flavors: `api` for production, `mock` for testing)

## 3. Core Directory Structure & Responsibilities

```
/
├── app/                                    # Android application module (UI/presentation layer)
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   │   └── liou/rayyuan/ebooksearchtaiwan/
│   │   │   │       ├── EBookSearchApplication.kt        # Application class (Koin DI setup)
│   │   │   │       ├── BaseActivity.kt                  # Base Activity for common functionality
│   │   │   │       ├── booksearch/                      # Main book search feature
│   │   │   │       │   ├── BookSearchActivity.kt        # Main launcher activity
│   │   │   │       │   ├── BookSearchViewModel.kt       # ViewModel for search logic
│   │   │   │       │   ├── BookSearchScreen.kt          # Compose UI screens
│   │   │   │       │   ├── composable/                  # Reusable Compose components
│   │   │   │       │   ├── list/                        # List-related UI models
│   │   │   │       │   ├── review/                      # Play Store review helper
│   │   │   │       │   ├── screen/                      # Screen compositions
│   │   │   │       │   └── viewstate/                   # UI state models
│   │   │   │       ├── bookstorereorder/                # Book store ordering feature
│   │   │   │       ├── camerapreview/                   # Barcode scanner feature
│   │   │   │       │   ├── BarcodeScanner.kt            # Main scanner composable
│   │   │   │       │   ├── CameraPreviewActivity.kt     # Camera activity
│   │   │   │       │   ├── permission/                  # Camera permission handling
│   │   │   │       │   └── preview/                     # Camera preview UI
│   │   │   │       ├── composable/                      # Shared Compose components
│   │   │   │       ├── di/                              # Dependency injection modules (Koin)
│   │   │   │       │   ├── AppModule.kt                 # Main app DI module
│   │   │   │       │   └── BarcodeScannerModule.kt      # Scanner-specific DI
│   │   │   │       ├── domain/                          # App-specific domain implementations
│   │   │   │       ├── interactor/                      # Business logic interactors
│   │   │   │       ├── misc/                            # Utilities (deeplink, analytics)
│   │   │   │       ├── preferencesetting/               # Settings screen
│   │   │   │       ├── simplewebview/                   # WebView component
│   │   │   │       ├── ui/                              # UI theme and utilities
│   │   │   │       │   ├── theme/                       # Material 3 theme definitions
│   │   │   │       │   └── composables/                 # Device orientation utilities
│   │   │   │       ├── utils/                           # Android-specific utilities
│   │   │   │       └── view/                            # Navigation/router
│   │   │   ├── res/                                     # Android resources (layouts, strings, drawables)
│   │   │   └── AndroidManifest.xml                      # Android app manifest
│   │   ├── debug/                                       # Debug-specific resources
│   │   └── androidTest/                                 # Android instrumentation tests
│   ├── build.gradle.kts                                 # App module Gradle configuration
│   └── google-services.json                             # Firebase configuration
│
├── commonMain/                                          # Shared business logic module (domain + data layers)
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   │   └── com/rayliu/commonmain/
│   │   │   │       ├── data/                            # Data layer
│   │   │   │       │   ├── api/                         # Network API services
│   │   │   │       │   │   ├── BookSearchService.kt     # Book search API client
│   │   │   │       │   │   └── BookStoresService.kt     # Book stores API client
│   │   │   │       │   ├── dao/                         # Data Access Objects
│   │   │   │       │   │   └── SearchRecordDao.kt       # Search history DAO
│   │   │   │       │   ├── dto/                         # Data Transfer Objects (network models)
│   │   │   │       │   ├── mapper/                      # Data mappers (DTO ↔ Domain)
│   │   │   │       │   └── database/                    # Database schema (SQLDelight)
│   │   │   │       ├── domain/                          # Domain layer (business logic)
│   │   │   │       │   ├── model/                       # Domain models (Book, BookStore, etc.)
│   │   │   │       │   ├── repository/                  # Repository interfaces & implementations
│   │   │   │       │   │   ├── BookRepository.kt        # Book data repository
│   │   │   │       │   │   ├── BookStoreDetailsRepository.kt
│   │   │   │       │   │   ├── SearchRecordRepository.kt
│   │   │   │       │   │   └── BrowseHistoryRepository.kt
│   │   │   │       │   ├── service/                     # Domain services
│   │   │   │       │   └── usecase/                     # Use cases (business operations)
│   │   │   │       │       ├── GetBooksWithStoresUseCase.kt
│   │   │   │       │       ├── GetSearchRecordsUseCase.kt
│   │   │   │       │       └── ...                      # Other use cases
│   │   │   │       ├── di/                              # Dependency injection modules
│   │   │   │       │   ├── DomainModule.kt              # Domain layer DI (repositories, use cases)
│   │   │   │       │   ├── DispatcherModule.kt          # Coroutine dispatchers
│   │   │   │       │   ├── JsonModule.kt                # JSON serialization setup
│   │   │   │       │   └── MiscModule.kt                # Miscellaneous dependencies
│   │   │   │       └── ...                              # Utility classes (LevenshteinDistanceHelper, etc.)
│   │   │   └── sqldelight/
│   │   │       └── com/rayliu/commonmain/data/database/
│   │   │           └── SearchRecords.sq                 # SQLDelight schema & queries
│   │   ├── api/                                         # API flavor source (production API implementations)
│   │   │   └── java/com/rayliu/commonmain/data/api/
│   │   │       ├── BookSearchApi.kt                     # Real API implementation
│   │   │       ├── BookStoresApi.kt
│   │   │       └── di/DataModule.kt                     # API flavor DI module
│   │   ├── mock/                                        # Mock flavor source (test data)
│   │   │   ├── assets/                                  # Mock JSON data files
│   │   │   └── java/com/rayliu/commonmain/data/
│   │   │       ├── BookSearchApi.kt                     # Mock API implementation
│   │   │       ├── BookStoresApi.kt
│   │   │       └── di/DataModule.kt                     # Mock flavor DI module
│   │   └── test/                                        # Unit tests
│   ├── build.gradle.kts                                 # CommonMain module Gradle configuration
│   ├── consumer-rules.pro                               # ProGuard rules for library consumers
│   └── proguard-rules.pro                               # ProGuard rules
│
├── buildSrc/                                            # Gradle build configuration source
│   └── src/main/java/
│       └── AppSettings.kt                               # App version and SDK constants
│
├── gradle/
│   └── libs.versions.toml                               # Version catalog (dependency versions)
│
├── build.gradle.kts                                     # Root project Gradle configuration
├── settings.gradle.kts                                  # Gradle module settings (includes :app, :commonMain)
├── deteket-config.yml                                   # Detekt code quality configuration
├── gradle.properties                                    # Gradle properties
└── README.md                                            # Project documentation
```

## 4. Key Entry Points & Configuration

* **Application Entry Point:** 
  * `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/EBookSearchApplication.kt` - Application class that initializes Koin DI
  * `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchActivity.kt` - Main launcher activity (declared in AndroidManifest.xml)

* **Shared Logic Entry:** 
  * `commonMain/src/main/kotlin/com/rayliu/commonmain/` - Core business logic, data layer, and domain models

* **Main Configuration Files:**
  * `settings.gradle.kts` - Module definitions (`:app`, `:commonMain`)
  * `gradle/libs.versions.toml` - Centralized dependency version management
  * `buildSrc/src/main/java/AppSettings.kt` - App version and SDK constants
  * `app/src/main/AndroidManifest.xml` - Android app configuration, permissions, activities
  * `app/build.gradle.kts` - App module build configuration (Compose, Firebase, etc.)
  * `commonMain/build.gradle.kts` - Shared module build configuration (Ktor, SQLDelight, etc.)

* **Product Flavors:**
  * `api` - Production flavor using real API endpoints
  * `mock` - Testing flavor using mock JSON data from `commonMain/src/mock/assets/`

* **Database:**
  * SQLDelight schema: `commonMain/src/main/sqldelight/com/rayliu/commonmain/data/database/SearchRecords.sq`
  * Database name: `ebooktw_database`
  * Stores search history records

* **Dependency Injection:**
  * Koin modules are organized by layer:
    * `app/di/AppModule.kt` - App-level dependencies (ViewModels, app-specific services)
    * `commonMain/di/DomainModule.kt` - Domain layer (repositories, use cases)
    * `commonMain/di/DataModule.kt` - Data layer (API clients, database) - flavor-specific
    * `commonMain/di/JsonModule.kt` - JSON serialization configuration
    * `commonMain/di/DispatcherModule.kt` - Coroutine dispatchers

* **Architecture Pattern:**
  * Clean Architecture with MVVM
  * **Presentation Layer** (`app/`): Activities, Compose screens, ViewModels
  * **Domain Layer** (`commonMain/domain/`): Use cases, domain models, repository interfaces
  * **Data Layer** (`commonMain/data/`): Repository implementations, API services, database DAOs, mappers
</project_context>