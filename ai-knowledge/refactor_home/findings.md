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
