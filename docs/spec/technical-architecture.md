# 技術架構與責任邊界

## 穩定責任方向

`ARCH-001` `app` 負責 Android UI、Activity 與 Compose 畫面、平台生命週期、權限、相機、Custom Tab、WebView、分享、評分及使用者操作協調，也負責 AppFunctions Service、AppFunction DTO 與 PendingIntent 建立。

`ARCH-002` `commonMain` 負責搜尋與書店 API、網路 DTO 映射、領域模型、Repository、Use Case、結果排序語意及可跨畫面共用的持久化存取。

`ARCH-003` 依賴方向應讓 Android 畫面呼叫領域能力，不應讓領域規則依賴特定 Compose 畫面或 Activity。未來可重整模組名稱，但必須保留 UI 與領域、資料責任的分離。

## 目前資料流

一般搜尋的主要資料流如下：

```text
使用者輸入
  → BookSearchActivity / Compose 畫面
  → BookSearchViewModel
  → GetBooksWithStoresUseCase
  → BookRepository
  → Ktor API
  → DTO Mapper
  → BookStores 領域模型
  → 最佳結果與各書店區段
```

快照讀取沿用相同的映射與顯示流程，但入口是 `GetSearchSnapshotUseCase`，且語意遵循 `SNAPSHOT-003`，不得重新送出關鍵字搜尋。

系統 agent
  → EbookAppFunctionService
  → SearchBooksUseCase / GetSlicedSearchSnapshotUseCase /
    GetRecentSearchRecordsUseCase
  → 既有 Repository 與結果切片

`ARCH-004` ViewModel 必須以最新請求為畫面狀態來源，管理載入、成功、部分成功及錯誤狀態，並防止已取消請求覆蓋新結果。

`ARCH-005` 網路 DTO 不應直接成為 UI 的永久資料模型。外部可空欄位及錯誤狀態需先映射成 App 可處理的領域語意。

## 本機資料

`ARCH-006` 搜尋紀錄目前由 SQLDelight 保存並透過 Paging 讀取。其穩定契約是 `HISTORY-001` 至 `HISTORY-006`，不是 SQL schema 或 Paging 類別名稱。

`ARCH-007` 書店啟用與順序、評分邀請狀態目前由 Preferences DataStore 保存；既有 SharedPreferences 資料可遷移至 DataStore。

`ARCH-008` 主題、跟隨系統、價格排序及 Custom Tab 偏好目前由 Android SharedPreferences 保存。替換儲存技術時必須保留預設值、跨重啟行為及使用者既有資料。

## 當前框架角色

| 技術 | 當前角色 | 是否為產品契約 |
| --- | --- | --- |
| Jetpack Compose | 主要 UI | 否 |
| Koin | 依賴注入 | 否 |
| Ktor | HTTP client 與 JSON | 否 |
| Kotlin Serialization | API DTO 解析 | 否 |
| SQLDelight | 搜尋紀錄資料庫 | 否 |
| Preferences DataStore | 書店順序與部分狀態 | 否 |
| SharedPreferences | 畫面偏好 | 否 |
| Paging | 搜尋紀錄分頁 | 否 |
| CameraX 與 ML Kit | 相機與 EAN-13 辨識 | 否 |

替換框架可以接受，但必須保持相關 Requirement 的可觀察行為與資料遷移。

## Build Variant

`ARCH-009` `apiRelease` 是正式產品變體，使用正式 API host、啟用縮減與 release 簽章流程。

`ARCH-010` `apiDebug` 是連接 API 的開發變體，具有 debug application ID suffix，並停用 Firebase Analytics 收集。

`ARCH-011` `mockDebug` 使用 repository 內的固定資產供開發與測試，不代表正式後端行為。`mockRelease` 不建立。

`ARCH-012` Java 與 Kotlin 編譯基準為 JDK 17；Android 最低支援 API 26。Compile SDK 與 Target SDK 以當前建置設定為準。

## 主要實作入口

模組入口是 `app/build.gradle.kts`、`commonMain/build.gradle.kts` 及兩模組的 DI 定義。搜尋責任可從 `BookSearchViewModel`、`GetBooksWithStoresUseCase`、`BookRepositoryImpl` 與 mapper 追查。Agent 應引用 Requirement ID，不要把此處列出的 class 當成不可移動的規格。
