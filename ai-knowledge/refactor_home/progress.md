# Progress — BookSearch Adaptive UI

> 執行時更新本檔。Task 細節見 [implementation_plan.md](./implementation_plan.md)。

## Status

| 欄位 | 值 |
| --- | --- |
| 總體狀態 | 自動化收尾完成；Journey A–F 待人工裝置驗證 |
| 目前里程碑 | M3 |
| 目前 Task | Task 9 complete（人工驗收 pending） |
| 最後更新 | 2026-08-09 |

## Milestones

- [x] **M1** Adaptive 骨架 + 導流單一真相
- [x] **M2** 縮放狀態 + chrome 整理
- [ ] **M3** 驗證 + Spec（自動化與 Spec 完成；人工裝置旅程未完成）

## Tasks

- [x] Task 1: Compact 判定 helper + Activity 導流改接 `WindowSizeClass`
- [x] Task 2: `NavigableListDetailPaneScaffold` 遷移與返回／focus
- [x] Task 3: 移除 `isTabletSize` / `LocalDeviceInfo`（若可）
- [x] Task 4: Detail 空狀態與關閉鈕 chrome
- [x] Task 5: 搜尋紀錄 overlay 與清單大螢幕小修
- [x] Task 6: 主流程 contentDescription 字串資源化
- [x] Task 7: Compose UI 測試（以 JVM 導流契約測試完成）
- [x] Task 8: Spec patch 套用（需核准）
- [x] Task 9: 自動化驗證與收尾（Journey A–F 保留給人工裝置驗收）

## Blockers

| 日期 | 阻礙 | 狀態 | 備註 |
| --- | --- | --- | --- |
| — | — | — | — |

## Verification log

| 日期 | 項目 | 結果 | 備註 |
| --- | --- | --- | --- |
| 2026-08-09 | `:app:compileApiDebugKotlin` | 通過 | JDK 17；有既有編譯警告 |
| 2026-08-09 | Task 4 `:app:compileApiDebugKotlin` | 通過 | `DetailPaneEmptyState` 抽出後編譯成功 |
| 2026-08-09 | Task 6 `:app:compileApiDebugKotlin` | 通過 | 主流程 contentDescription 字串資源化 |
| 2026-08-09 | `./gradlew test lint assembleApiDebug` | 通過 | JDK 17.0.7；168 tasks；Lint 仍有 1 error、95 warnings，不是 Lint clean |
| 2026-08-09 | `BookSearchAdaptiveNavigationTest` | 通過 | 2 tests、0 failures／errors／skipped |
| 2026-08-09 | `./gradlew spotlessCheck` | 初次失敗 | 僅 `DetailPaneEmptyState.kt` 格式違規；已執行 `:app:spotlessKotlinApply` 修正 |
| 2026-08-09 | `./gradlew spotlessCheck`（修正後） | 通過 | JDK 17；全專案 Spotless check successful |

## Notes

- 開工前重讀 Spike 與 `design_guidelines.md`。
- 踩坑寫入 [findings.md](./findings.md)。
- **Task 5**：搜尋紀錄 overlay 已自然限定在 list pane（`NavigableListDetailPaneScaffold` 內 `AnimatedPane` → `BookResultListScreen` content `Box`）；無需產品程式碼變更。Journey E 實機互動驗證延至 Task 9。

## Manual device verification（Spike §7.1）

以下皆未由本次 CLI 工作假裝勾選，需以 Emulator／實機（含 Android Desktop Mode 可調視窗）完成：

- [ ] Journey A：Compact + Custom Tab
- [ ] Journey B：Compact + in-app WebView
- [ ] Journey C：Medium+ 分欄
- [ ] Journey D：Desktop 跨 Compact↔Medium↔Expanded 縮放並保留 detail
- [ ] Journey E：搜尋紀錄 overlay 不擋 detail
- [ ] Journey F：快照選單／設定入口回歸
- [ ] 手機直向與 TalkBack `contentDescription` 抽樣
