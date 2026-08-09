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
