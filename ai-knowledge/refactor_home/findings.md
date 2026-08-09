# Findings — 實作踩坑紀錄

> 開發過程中補上。記錄問題、原因、解法與如何避免再犯。  
> 一開始刻意留白；無內容時保持下列範本即可。

## 怎麼記

每筆建議包含：

- **日期**
- **症狀**（看到什麼）
- **原因**（根因）
- **解法**（做了什麼）
- **避免再犯**（慣例／檢查項）
- **相關檔案**（若有）

---

## Log

### 2026-08-09 — WindowSizeClass breakpoint API 尚未包含於目前依賴

- **症狀**：brief 範例的 `WindowSizeClass.isWidthAtLeastBreakpoint()` 與 `WIDTH_DP_MEDIUM_LOWER_BOUND` 無法編譯。
- **原因**：Compose BOM `2025.10.00` 的 Adaptive `1.1.0` 實際解析 `androidx.window:window-core-android:1.3.0`；該 API 是較新 Window Core 才提供。
- **解法**：使用該版本官方等效 API：`windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT`，維持「未達 Medium = Compact」語意。
- **避免再犯**：升級 Adaptive／Window Core 後，可改用非 deprecated 的 breakpoint API；實作前以 `dependencyInsight` 確認實際解析版本。
- **相關檔案**：`app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/util/WindowWidthAdaptive.kt`

### 2026-08-09 — Navigable scaffold 位於 navigation 套件

- **症狀**：原本的 `ListDetailPaneScaffold` 從 `adaptive.layout` 匯入並分別接收 `directive`、`value`；替代 API 不在相同套件。
- **原因**：Adaptive 1.1.0 將 `NavigableListDetailPaneScaffold` 放在 `androidx.compose.material3.adaptive.navigation`，並直接接收 `ThreePaneScaffoldNavigator`。
- **解法**：改由 `adaptive.navigation` 匯入，傳入 `navigator = paneNavigator`，讓 scaffold 整合 pane navigation／predictive back；App 的 `BackHandler` 僅保留搜尋框 focus 行為。
- **避免再犯**：遷移 adaptive canonical scaffold 時，同時確認 artifact 版本、package 與參數，不要假設 navigable 版本仍屬於 layout package。
- **相關檔案**：`app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`

### 2026-08-09 — BackHandler 須在 scaffold 之後 compose 才優先於 pane back

- **症狀**：搜尋框有 focus 且 compact 開啟 detail 時，system back 可能先觸發 pane `navigateBack` 而非取消 focus。
- **原因**：Compose 以反向註冊順序呼叫 back callback；focus `BackHandler` 若在 `NavigableListDetailPaneScaffold` 之前 compose，scaffold 內建 handler 會先執行。
- **解法**：將 focus-only `BackHandler` 移到 scaffold 之後 compose，讓搜尋 unfocus 優先於 pane back。
- **避免再犯**：App 自訂 back 與 navigable scaffold 並存時，確認 composition 順序；後 compose 的 handler 先被呼叫。
- **相關檔案**：`app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`

### 2026-08-09 — Task 5：搜尋紀錄 overlay 已 pane-scoped，無需程式碼變更

- **症狀**：Task 5 要求確認搜尋紀錄 overlay 不會覆蓋 Medium+ detail pane，並檢查清單大螢幕間距。
- **原因**：`BookResultListScreen` 位於 `NavigableListDetailPaneScaffold.listPane` 的 `AnimatedPane` 內；搜尋紀錄卡片與 scrim 是 content `Box` 子項，`fillMaxSize()` 只填滿 list pane 邊界。
- **解法**：靜態檢查 Compose hierarchy 與 modifier scope 後確認 scope 正確；未修改產品 Kotlin/XML。Journey E 實機驗證延至 Task 9。
- **避免再犯**：遷移 adaptive scaffold 後，overlay 類 UI 先確認是否已在 pane 內 compose，再決定是否需要額外 scope 限制。
- **相關檔案**：`BookSearchScreen.kt`、`BookResultListScreen.kt`

### 2026-08-09 — Task 7：以 JVM 契約測試覆蓋導流分支

- **症狀**：完整 `BookSearchActivity` instrumentation 會載入廣告、網路與 Koin；本機唯一連接裝置又是 `unauthorized`，無法穩定執行。
- **原因**：待測契約其實只由 Custom Tab 偏好與 `isWidthCompact` 兩個 Boolean 決定，不需要啟動完整 Compose Activity。
- **解法**：抽出 Activity 實際使用的 `shouldOpenCustomTab()` 純函式，以 `testApiDebugUnitTest` 驗證 Compact 開 Custom Tab、Medium+ 固定走 detail。
- **避免再犯**：導流條件維持單一純決策點；Activity 僅依結果執行 Custom Tab 或 pane navigation side effect。
- **相關檔案**：`BookSearchActivity.kt`、`BookSearchAdaptiveNavigationTest.kt`

### 2026-08-09 — Task 9：Detail 空狀態有既存 Spotless 違規

- **症狀**：CI-like 基準成功，但額外執行 `spotlessCheck` 時在 `DetailPaneEmptyState.kt` 失敗。
- **原因**：Composable 函式參數換行不符合專案目前 KtLint／Spotless 的格式輸出。
- **解法**：執行 `:app:spotlessKotlinApply`；實際 diff 只格式化該函式宣告。
- **避免再犯**：新增 Kotlin 檔案後，在收尾基準之外一併執行 `spotlessCheck`。
- **相關檔案**：`app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/composable/DetailPaneEmptyState.kt`
