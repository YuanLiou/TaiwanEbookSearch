# Task Plan — Compose Adaptive UI（BookSearch / Desktop Mode）

> **權威設計：** [Spike Report](../spike/2026-08-09-compose-adaptive-ui-desktop.md)  
> **執行細節：** [implementation_plan.md](./implementation_plan.md)  
> **進度：** [progress.md](./progress.md)

## Goal

讓 `BookSearch` 主流程以 Material 3 官方 Adaptive（`NavigableListDetailPaneScaffold` + `WindowSizeClass`）在手機、平板與 Android Desktop Mode 可調視窗下可用：分欄正確、導流符合 `NAV-001`–`NAV-004`、縮放不丟已選商品狀態，並完成主流程畫面整理。

## Scope

### In

- `BookSearchActivity` / `BookSearchScreen` list–detail 骨架遷移
- Custom Tab 門檻改為 Compact（官方 `WindowSizeClass`），移除 `isTabletSize`
- 主流程 chrome／可讀性／搜尋紀錄 overlay／WebView detail 整理
- 主流程內明顯硬編碼 contentDescription → 三語系字串
- 手動驗證清單 + 建議 Compose UI 測試
- 核准後套用 Spec patch（`LIMIT-003`、NAV 用語）

### Out（見 Spike §8 Follow-up）

- 設定／書店排序／相機 Adaptive
- 滑鼠／鍵盤完整互動
- 清單 `maxWidth`
- FoldingFeature 實作
- 單 Activity / NavigationSuite 大重構
- Rank-app `FIXME`（除非實作中順手且不擴大範圍）

## Milestones

| ID | 里程碑 | 完成定義 |
| --- | --- | --- |
| M1 | Adaptive 骨架 + 導流單一真相 | `NavigableListDetailPaneScaffold`；Compact 才可 Custom Tab；無 `isTabletSize` |
| M2 | 縮放狀態 + chrome 整理 | 寬↔窄保留 detail；空狀態／返回／overlay／字串可用 |
| M3 | 驗證 + Spec | 手測通過；建議 UI 測試落地；Spec patch 經核准後套用 |

建議順序：M1 → M2 → M3。細節 Task 見 `implementation_plan.md`。

## Deliberate behavior change

橫向／Desktop 約 **600–840dp**：舊 `isTabletSize` 偏單欄 → 新邏輯 **Medium+ 分欄 + 右側 WebView**。必須寫進 PR 與 Spec 變更說明。

## Definition of Done

- [ ] Spike 成功標準達成（分欄、NAV、縮放狀態）
- [ ] 主流程無 `isTabletSize`／`LocalDeviceInfo` 依賴（若全專案無引用則刪除）
- [ ] `progress.md` 對應 Task 勾選完成
- [ ] 手動驗證清單完成（見 `implementation_plan.md` / Spike §7）
- [ ] Spec 已同步或明確記為「待核准套用」且不與 Code 漂移
- [ ] 踩坑記入 `findings.md`（若有）

## Document map

| 檔案 | 角色 |
| --- | --- |
| [task_plan.md](./task_plan.md) | 本檔：目標、範圍、里程碑 |
| [implementation_plan.md](./implementation_plan.md) | 可執行 Task／步驟 |
| [progress.md](./progress.md) | 執行時勾選與阻礙 |
| [design_guidelines.md](./design_guidelines.md) | 實作約束與最佳實踐 |
| [user_journey.md](./user_journey.md) | 體驗劇本／手測對照 |
| [findings.md](./findings.md) | 實作踩坑（開發時補） |
| [../spike/2026-08-09-compose-adaptive-ui-desktop.md](../spike/2026-08-09-compose-adaptive-ui-desktop.md) | 已核准 Spike |

## Notes for agents

- 對使用者使用台灣繁體中文。
- **先讀 Spike，再改 Code**；行為變更須對齊 Spec／patch 草稿。
- 僅在使用者明確要求時 commit；建議 commit 訊息見各 Task，並加 `Assisted-by` trailer（見 repo 規則）。
- 實作時維護 `progress.md` 與 `findings.md`。
