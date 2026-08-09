# Progress — BookSearch Adaptive UI

> 執行時更新本檔。Task 細節見 [implementation_plan.md](./implementation_plan.md)。

## Status

| 欄位 | 值 |
| --- | --- |
| 總體狀態 | In progress |
| 目前里程碑 | M1 |
| 目前 Task | Task 9 |
| 最後更新 | 2026-08-09 |

## Milestones

- [ ] **M1** Adaptive 骨架 + 導流單一真相
- [ ] **M2** 縮放狀態 + chrome 整理
- [ ] **M3** 驗證 + Spec

## Tasks

- [x] Task 1: Compact 判定 helper + Activity 導流改接 `WindowSizeClass`
- [x] Task 2: `NavigableListDetailPaneScaffold` 遷移與返回／focus
- [x] Task 3: 移除 `isTabletSize` / `LocalDeviceInfo`（若可）
- [x] Task 4: Detail 空狀態與關閉鈕 chrome
- [x] Task 5: 搜尋紀錄 overlay 與清單大螢幕小修
- [x] Task 6: 主流程 contentDescription 字串資源化
- [ ] Task 7: Compose UI 測試（導流分支）
- [x] Task 8: Spec patch 套用（需核准）
- [ ] Task 9: 手動驗證與收尾

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

## Notes

- 開工前重讀 Spike 與 `design_guidelines.md`。
- 踩坑寫入 [findings.md](./findings.md)。
- **Task 5**：搜尋紀錄 overlay 已自然限定在 list pane（`NavigableListDetailPaneScaffold` 內 `AnimatedPane` → `BookResultListScreen` content `Box`）；無需產品程式碼變更。Journey E 實機互動驗證延至 Task 9。
