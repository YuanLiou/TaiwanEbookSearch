### Task 9: 手動驗證與收尾

**Files:**
- Update: `ai-knowledge/refactor_home/progress.md`
- Update: `ai-knowledge/refactor_home/findings.md`（若有坑）

- [ ] **Step 1: 跑基線 Gradle**

Run: `./gradlew test lint assembleApiDebug`

Expected: 建置成功；記錄已知 Lint 不阻擋（勿宣稱零問題）

- [ ] **Step 2: 手動旅程（對照 user_journey.md）**

- [ ] Journey A Compact + Custom Tab
- [ ] Journey B Compact + in-app WebView
- [ ] Journey C Medium+ 分欄
- [ ] Journey D Desktop 縮放保留 detail
- [ ] Journey E 搜尋紀錄 overlay
- [ ] Journey F 快照／設定回歸

- [ ] **Step 3: 更新 `progress.md` 里程碑與驗證 log**
- [ ] **Step 4: 向使用者回報完成範圍與未驗證項**

---

## Manual verification checklist（彙整）

- [ ] Compact + Custom Tab 開 → 外開，不進 detail
- [ ] Compact + Custom Tab 關 → 單欄 WebView，返回回清單
- [ ] Medium／Expanded → 分欄；點書右側 WebView；偏好不影響
- [ ] Desktop 可調視窗跨 Compact↔Medium↔Expanded
- [ ] 已選書縮窄 → 仍顯示該書；返回回清單
- [ ] 未選書分欄 → 右側空狀態
- [ ] 搜尋紀錄 overlay 不擋 detail
- [ ] 手機直向回歸
- [ ] contentDescription 已資源化（抽樣）

## Self-review（寫 plan 時已對 Spike）

| Spike 要求 | Task |
| --- | --- |
| Navigable + WindowSizeClass | 1–2 |
| 移除 isTabletSize | 1、3 |
| 縮放保留 detail | 2（navigator 預設）+ 9 手測 |
| Chrome／overlay／a11y | 4–6 |
| UI 測試 + 手測 | 7、9 |
| Spec patch | 8 |
| Follow-up 不實作 | Global Constraints |

## Execution handoff

Plan 已拆分並存放於 `ai-knowledge/refactor_home/`。

實作時可選：

1. **Subagent-Driven（建議）** — 每 Task 新 subagent，Task 間審查  
2. **Inline Execution** — 本會話依 `executing-plans` 批次執行並設檢查點  

開始實作前請使用者明確選擇，並確認是否允許在各 Task 結束時 commit。
