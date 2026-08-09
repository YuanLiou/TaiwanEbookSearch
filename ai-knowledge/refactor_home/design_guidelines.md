# Design Guidelines — BookSearch Adaptive UI

對齊 Spike 與產品 Spec。實作時若與本檔衝突，以 **已核准 Spike** 與 **`docs/spec` 現況基線（含已套用的 patch）** 為準。

## 1. Adaptive 單一真相

- 使用 Material 3 Adaptive：`currentWindowAdaptiveInfo()`／`WindowSizeClass`、`NavigableListDetailPaneScaffold`、`rememberListDetailPaneScaffoldNavigator`。
- **斷點：** Compact = 單欄；Medium 及以上 = 分欄（官方預設 directive，不自訂門檻除非產品另核准）。
- **禁止**再引入方向相關的自訂 `isTabletSize()`（直向 600／橫向 840）作為導流或分欄依據。
- Custom Tab 僅在 **寬度 Compact** 且使用者偏好開啟時使用；Medium+ 一律 in-app detail（`NAV-003`）。

## 2. 導流（NAV）

| 規則 | 要求 |
| --- | --- |
| `NAV-001` | Compact + 偏好 Custom Tab → 瀏覽器分頁 |
| `NAV-002` | Compact + 關閉偏好 → in-app WebView |
| `NAV-003` | 分欄（Medium+）→ 右側 WebView，忽略 Custom Tab 偏好 |
| `NAV-004` | WebView 可回清單、可分享、可外部瀏覽器開啟 |

縮放：Medium+ → Compact 時**保留**已選 detail；使用者返回才回清單。

## 3. UI／版面

- pane 內容預設 **撐滿** 所屬 pane（不做清單 `maxWidth`；該項為 Follow-up）。
- 搜尋與快照／設定選單留在 **list pane** TopAppBar；書名／作者／分享／外部開啟留在 **detail** TopAppBar。
- 搜尋紀錄 overlay **不得**擋住可操作的 detail WebView（遮罩範圍應限於 list pane 或等價合理行為）。
- 空狀態 detail：未選書時顯示整理過的 placeholder，不是空白或錯誤感頁面。
- 單欄顯示 detail 時提供關閉／返回；雙欄且 detail 可見時隱藏關閉鈕（語意同現況）。

## 4. 字串與無障礙

- 使用者可見文字與重要 `contentDescription`：**不得硬編碼**；同步維護 `values`、`values-zh-rTW`、`values-zh-rCN`。
- 本次主範圍：修好搜尋主流程內明顯硬編碼（見 `implementation_plan` Task）；不強制清整個 app。

## 5. 架構邊界

- ViewModel **不**持有 pane navigator 狀態。
- 維持多 Activity；不把設定／相機併進單 Activity shell。
- `app` 負責 Compose／Activity；領域規則不依賴特定 Adaptive composable。
- Experimental API：`@OptIn(ExperimentalMaterial3AdaptiveApi::class)` 可接受；升級 BOM 時專測 list–detail。

## 6. YAGNI／範圍控制

不要順便做：

- FoldingFeature／鉸鏈
- 滑鼠右鍵、鍵盤快捷鍵體系
- 書卡 grid、資訊架構重做
- NavigationSuite 全局殼層
- Rank-app `FIXME`（除非獨立小修且使用者同意）

踩到範圍外需求 → 記 `findings.md` 或 Spike Follow-up，不自行擴大 Task。

## 7. Spec 同步

- Code 刻意改變大畫面判定時，必須更新 `LIMIT-003` 與 NAV 用語（見 Spike §6 patch 草稿）。
- 套用 Spec 前需使用者／負責人核准；未套用前 PR 說明須標明 deliberate change。
- 使用 `.agents/skills/align-code-and-spec/SKILL.md` 做對焦（預設唯讀 audit；明確要求才改檔）。

## 8. 驗證態度

- `./gradlew test lint assembleApiDebug` 是基線，**不是** Desktop 行為驗收。
- Desktop／可調視窗以手動為準；Compose UI 測試覆蓋可測的導流分支。
- 完成斷言前更新 `progress.md` 驗證區。
