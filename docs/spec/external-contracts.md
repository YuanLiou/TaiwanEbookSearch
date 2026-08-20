# 外部契約

本文件只描述 Android App 依賴的外部行為。後端內部爬蟲、資料庫、排程及部署方式不屬於本 repository 的規格。

## 搜尋 API

`API-001` 即時搜尋使用 `POST searches`，查詢參數 `q` 傳遞使用者原始搜尋字串，重複參數 `bookstores[]` 傳遞目前啟用且依使用者設定排序的來源識別。

`API-002` 快照讀取使用 `GET searches/{searchId}`。此操作必須回傳保存的歷史搜尋內容，不得在 App 端轉換成新的即時搜尋。

`API-003` 搜尋回應的頂層資料可包含 API 版本、快照 ID、原始關鍵字、搜尋時間、處理時間、總數及各來源結果。App 的核心依賴是原始關鍵字、快照 ID 與各來源結果。

`API-004` 每個來源結果必須能識別書店、服務狀態、是否成功、錯誤訊息及商品陣列。單一來源失敗不得使其他來源的成功資料失效，詳見 `SEARCH-011`。

`API-005` API 回傳的商品順序具有語意。每個來源第一筆是最高相關性候選，後續項目相關性逐步降低。App 不得在建立「最佳結果」前先任意改變這個順序。

`API-006` 可顯示商品的必要語意是書名、來源及有效商品連結。價格與幣別可以未知；封面、作者、譯者、出版社、簡介及出版日期皆可缺少。

`API-007` `price` 是 API 提供的數值，`priceCurrency` 是其貨幣代碼。App 不換算貨幣。價格為 `-1` 時代表未知或無法取得，不代表免費。

## 書店狀態 API

`API-008` 服務狀態使用 `GET bookstores`。每筆資料至少需提供穩定來源識別；顯示名稱、網站、是否在線及狀態由 API 提供。

`API-009` App 必須把 API 來源狀態與本機啟用設定分開解讀。API 離線或維修是遠端狀態，使用者停用是本機狀態，兩者不可互相覆蓋。

## 網路與錯誤

`API-010` App 必須為連線與請求設定有限逾時，不得無限等待。網路例外需映射為使用者可理解的無網路、逾時或一般連線錯誤。

`API-011` API host 與 port 由 Build Variant 的設定提供，不得把開發環境位址當成產品契約寫死在共用行為中。

## 第三方平台

`EXT-002` Google Play In-App Review 只接受 App 在符合 `PROD-011` 時提出邀請。是否顯示及其介面由 Google Play 決定。

`EXT-003` Firebase Analytics 與 Crashlytics 是正式版本的遙測及錯誤診斷整合。Debug 版本停用 Analytics 收集；遙測失敗不得阻斷核心搜尋流程。

`EXT-004` 商品頁、Android Custom Tabs、WebView、Sharesheet 與系統相機權限屬於 Android 平台整合。行為規格分別見導流與掃描文件，不要求固定使用目前的 Library。

`EXT-005` Android AppFunctions 是系統 agent 的裝置端整合。App 負責
登錄與執行四個已公開 function。系統或 Gemini 是否實際呼叫本 App
由平台決定，不是本產品可保證的契約。

## 主要實作入口

目前 API 介面與 DTO 主要位於 `commonMain/src/api` 及 `commonMain/src/main/kotlin/com/rayliu/commonmain/data`。第三方平台整合主要位於 `app/build.gradle.kts`、`EBookSearchApplication`、`PlayStoreReviewHelper` 與 AppFunctions Service。這些是追查入口，不是永久檔案契約。
