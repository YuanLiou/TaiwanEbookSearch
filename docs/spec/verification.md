# 基準驗證紀錄

驗證基準：commit `fbe2a4c`

驗證日期：2026-07-26

驗證環境：macOS、JDK 17、Pixel 8a Android Emulator

本文件是規格草案建立時的證據摘要，不是手寫 changelog。後續變更應依受影響 Requirement 執行相稱驗證，而不是把每次操作追加到此文件。

## 自動化驗證

執行命令：

```shell
./gradlew -Dorg.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home test lint assembleApiDebug
```

結果為 `BUILD SUCCESSFUL`，共執行 206 個 Gradle tasks，並成功產生 `apiDebug` APK。

現有 unit test 只執行兩個模組在三個可用 Variant 下的 `ExampleUnitTest`，共 6 次，全部通過。這不構成 `SEARCH-*`、`HISTORY-*`、`SCAN-*` 或 Deep Link 行為的自動化保證。

Lint task 完成，但報告存在 2 個 error 與 77 個 warning。兩個既有 error 是：

| 規則 | 位置 | 摘要 |
| --- | --- | --- |
| `SuspiciousModifierThen` | `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/bookstorereorder/composable/DraggableItem.kt:56` | Compose Modifier 串接警告 |
| `WrongGradleMethod` | `app/build.gradle.kts:105` | Firebase App Distribution Gradle DSL 警告 |

由於 `abortOnError = false`，不能把上述 Gradle 成功回報為 Lint clean。後續驗證至少不得新增未解釋的 Lint 問題。

編譯期間另有 annotation target、已棄用 Koin Android context 及 vibrator API 等 warning。它們未在本次 Spec 工作中修正。

## Emulator 行為驗證

安裝並啟動 `apiDebug` 後，已觀察到：

| 情境 | 結果 |
| --- | --- |
| 首次首頁 | 顯示 10 家來源的服務狀態、App 版本及廣告區 |
| 搜尋 `9789861755267` | 顯示「最佳結果」與各書店區段 |
| 最佳結果 | 第一批實際回應包含 Google Play 圖書與 Readmoo，依價格數值顯示 |
| 結果選單 | 提供複製快照網址、分享搜尋結果與設定 |
| 設定 | 可觀察到主題、價格排序、Custom Tab、歷史清除與書店排序入口 |
| 搜尋紀錄 | 搜尋欄取得焦點後顯示既有查詢 |
| 商品導流 | 點擊結果在手機模式開啟 Google Play Custom Tab |

這些觀察用於支持資訊架構與主要流程，不表示所有書店、錯誤狀態或裝置尺寸都已逐項驗證。

## 未完成的行為驗證

條碼掃描只確認程式碼把 ML Kit 限制為 EAN-13；本次未授予 Emulator 相機權限，也未用實體條碼完成端到端掃描。

本次未逐項重現逾時、無網路、部分來源失敗、快照 Deep Link、關鍵字 Deep Link、大畫面右側 WebView、三語系、深色模式、清除資料及 Google Play 評分是否實際顯示。

## Agent 驗證要求

規格或程式碼變更後，先從 Requirement ID 判斷受影響流程。至少執行相關 unit test、`lint` 與可建置 Variant；核心使用者行為需補做 Emulator 或實機操作。回報時必須分開說明已通過、因既有基線仍存在及未驗證的項目。
