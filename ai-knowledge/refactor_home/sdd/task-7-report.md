# Task 7 Report — Compose UI 導流分支測試

## Status

Complete. Commit: `110031b`.

## Implementation

- 抽出 `shouldOpenCustomTab(preferCustomTab, isWidthCompact)` 純函式，並由 `BookSearchActivity` 的實際點擊導流 gate 使用。
- 新增兩個 JVM 契約測試，覆蓋 Compact + Custom Tab 偏好，以及 Medium+ 忽略該偏好並走 detail 的分支。
- 此最小抽取維持 `NAV-001`、`NAV-003` 現有行為，不變更產品 Spec。

## TDD Evidence

- RED：`./gradlew :app:testApiDebugUnitTest --tests liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchAdaptiveNavigationTest`
  - Exit 1；兩處 `Unresolved reference 'shouldOpenCustomTab'`，符合尚未建立 routing API 的預期。
- GREEN：同一命令
  - Exit 0；`BUILD SUCCESSFUL`。
- Regression：`./gradlew :app:testApiDebugUnitTest :app:spotlessKotlinCheck`
  - `testApiDebugUnitTest` 完成；整體 Exit 1，僅因既有未修改檔 `DetailPaneEmptyState.kt` 的 Spotless 格式差異。
- `git diff --check`
  - Exit 0。

## Instrumentation

`adb devices` 顯示唯一裝置 `R3GYC000ATF unauthorized`，因此未執行 connected instrumentation。選用不需廣告、網路或 Koin Activity 啟動的 JVM 測試，作為 CI 穩定覆蓋。
