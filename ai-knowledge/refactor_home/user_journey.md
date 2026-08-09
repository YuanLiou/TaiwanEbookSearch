# User Journey — BookSearch Adaptive

用於理解體驗與對照手測。產品規則引用 Spec Requirement ID。

## 角色與情境

- **使用者：** 想在多家書店比價電子書的讀者。
- **裝置：** 手機（Compact）、平板／大窗（Medium+）、Android Desktop Mode 可調視窗。

## Journey A — 手機 Compact + Custom Tab（預設偏好）

1. 開啟 App → 看到書店服務列表／搜尋框（list 單欄）。
2. 輸入關鍵字搜尋 → 結果列表。
3. 點一本書 → **Custom Tab** 開啟商品頁（`NAV-001`）。
4. 關閉分頁 → 回到 App 清單；清單狀態應仍在。

**痛點（現況風險）：** 若誤用大畫面門檻，小窗 Desktop 可能被當成「平板」而改走 WebView。

## Journey B — Compact + 關閉「使用瀏覽器分頁」

1. 設定關閉 Custom Tab 偏好。
2. 搜尋後點書 → **App 內 WebView** 佔滿單欄（`NAV-002`）。
3. 按關閉／系統返回 → 回清單（`NAV-004`）。
4. 可從選單分享或用外部瀏覽器開啟。

## Journey C — 平板／寬窗分欄（Medium+）

1. 寬視窗開啟 → 左側清單、右側空狀態（placeholder）。
2. 點書 → 右側載入 WebView；**即使** Custom Tab 偏好為開，仍走右側（`NAV-003`）。
3. 再點另一本 → 右側換成新書。
4. 清單可繼續捲動／再搜尋；detail 標題列顯示書名／作者。

**痛點：** 搜尋紀錄半透明遮罩若蓋住整窗，會擋右側操作。

## Journey D — Desktop 視窗縮放（核心成功標準）

1. 寬窗選好一本書（右側 WebView）。
2. 把視窗拖窄到 Compact → **仍應看到該書 WebView**（單欄 detail），不是被清回清單、也不應突然跳出 Custom Tab。
3. 返回 → 回清單。
4. 再拉寬 → 若仍停留在清單且無選書，右側恢復空狀態；若導航堆疊仍有 detail，則恢復並排。

**痛點（舊實作）：** `isTabletSize` 與 scaffold 不同步 → 縮放時導流錯亂。

## Journey E — 搜尋紀錄

1. Focus 搜尋框 → 紀錄列表與遮罩出現在 **list** 區域。
2. 點紀錄 → 觸發搜尋並關閉 overlay。
3. 分欄時：操作 detail 不應被 list 的遮罩誤擋。

## Journey F — 快照與設定（回歸）

1. 有 `searchId` 時，list 選單可複製／分享快照。
2. 設定入口仍從 list 選單進入（獨立 Activity；本次不重做 Adaptive）。

## 手測對照表

| Journey | 主要檢查 |
| --- | --- |
| A | Compact + 偏好開 → Custom Tab |
| B | Compact + 偏好關 → 單欄 WebView + 返回 |
| C | Medium+ → 分欄、右側 WebView、偏好無效 |
| D | 寬↔窄保留 detail；返回合理 |
| E | overlay 不擋 detail |
| F | 快照選單、設定入口無回歸 |

詳細 checkbox 見 [implementation_plan.md](./implementation_plan.md) 驗證一節與 Spike §7。
