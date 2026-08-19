# TaiwanEbookSearch 現況規格

狀態：**現況基線**

基準 commit：`fbe2a4c`

基準日期：2026-07-26

核准日期：2026-07-26

產品名稱：台灣電子書搜尋

技術識別：`TaiwanEbookSearch`

## 本規格的用途

本規格描述基準 commit 可建置出的 Android App 現況，主要供 AI Agent 在修改與維護專案時判斷產品行為、外部契約與穩定技術邊界。它不描述未來產品規劃，也不替 repository 外的後端服務撰寫完整實作規格。

本文件已由專案負責人核准為「現況基線」，是產品行為的權威來源。刻意改變產品行為時，Code 與受影響 Requirement 必須同步更新；非刻意偏離時，應以本規格為準修正 Code。疑似缺陷仍不得只因存在於現況 Code 就升格為正式產品規則。

## 按任務載入

| 任務類型 | 必讀文件 |
| --- | --- |
| 產品目的、使用者、範圍、營運功能 | [product-overview.md](product-overview.md) |
| 關鍵字搜尋、結果整合、排序、部分成功 | [search-and-comparison.md](search-and-comparison.md) |
| AppFunctions、系統 agent、裝置端工具 | [app-functions.md](app-functions.md) |
| 相機、EAN-13、掃描後搜尋 | [barcode-scanning.md](barcode-scanning.md) |
| 商品頁、Custom Tab、WebView、分享、Deep Link、快照 | [navigation-sharing-deep-links.md](navigation-sharing-deep-links.md) |
| 搜尋紀錄、外觀、書店排序與行為設定 | [history-and-settings.md](history-and-settings.md) |
| 搜尋 API、服務狀態、第三方服務 | [external-contracts.md](external-contracts.md) |
| 模組、資料流、持久化、Build Variant | [technical-architecture.md](technical-architecture.md) |
| 已知限制與非規範性改善註記 | [known-limitations.md](known-limitations.md) |
| 建置、測試、Lint 與實機證據 | [verification.md](verification.md) |

不要因為任務可能相關就一次載入全部文件。先讀最接近的領域，只有跨領域變更才增加文件。

## Requirement ID

Requirement ID 是穩定引用，不代表執行順序。重要規則使用領域前綴，例如 `SEARCH-001`、`HISTORY-002`、`DEEPLINK-001`、`APPFN-001`。純實作細節與可自由調整的視覺細節不編號。

## 證據優先順序

草案建立時的判定順序是：可建置 App 的實際行為、執行中的程式碼與資料流、自動化測試、README 或商店文案。後兩者可能過期，不能單獨推翻實際行為。

疑似缺陷不自動固化成正式規則。已由專案負責人確認的現況行為列為 Requirement；仍有風險或語意限制的行為放入 [known-limitations.md](known-limitations.md)。
