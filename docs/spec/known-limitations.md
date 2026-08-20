# 已知限制與改善邊界

本文件記錄基準版本中已知但不應被誤解為理想產品規則的限制。除非另有 Requirement 明確要求，Agent 不得利用這些限制擴大任務範圍，也不得把它們當成驗收目標。

## 價格與貨幣

`LIMIT-001` 「最佳結果」與依價格排序目前直接比較 API 的原始價格數值。不同幣別之間沒有匯率換算，因此跨幣別順序不能視為真實購買成本排名。

`LIMIT-002` API 以 `-1` 表示未知價格。現有 TWD 顯示流程可能把負值格式化成 `0`，其他幣別也可能直接顯示負值；排序仍可能使用原始負值。這些狀況不代表商品免費。修改時應保留「未知」語意，不應把 `0` 固化為正式規則。

## Adaptive UI

`LIMIT-003` 大畫面／分欄判定以 Material 3 Adaptive 的 `WindowSizeClass` 為準：Compact 為單欄，Medium 及以上為分欄。Adaptive 1.1.0 的標準 directive 在 Medium 仍為單欄，因此 App 必須明確使用 `calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth`（或維持相同分欄語意的後續 API），不可依賴 navigator 預設值。

此判定來源可隨官方 Adaptive API 演進而調整，但必須維持 `NAV-001` 至 `NAV-004`：單欄可用 Custom Tab 偏好，分欄則在詳細區域顯示 App 內商品頁。

相較於舊版以螢幕方向與 600／840dp 門檻判定，寬度約 600–840dp 的橫向手機、小視窗 Desktop 或部分平板姿態，現改為 Medium+ 分欄（右側 WebView，不受 Custom Tab 偏好影響）。此為採用 Adaptive Medium 雙欄模式的刻意變更。

## 資料相關性

`LIMIT-004` API 每家書店第一筆只是該來源最高相關性候選，不保證就是同一本書。App 目前不做 ISBN 或書名的跨來源實體辨識，因此「最佳結果」是候選集合，不是已驗證為相同版本的商品集合。

`LIMIT-005` 每家來源最多顯示排除第一筆後的 10 筆。API 後段可能逐漸偏離搜尋意圖；目前 App 不提供完整結果瀏覽。

## 條碼與外部內容

`LIMIT-006` 掃描只限制 EAN-13，不判定該條碼是否為 ISBN。非書籍 EAN-13 仍會被當成一般搜尋字串。

`LIMIT-007` 商品頁內容、可用性、登入、地區限制與購買流程由各書店控制。App 無法保證外部頁面長期可開啟，也不應解析頁面來宣稱購買成功。

## 文件與測試

`LIMIT-008` README、Fastlane 商店文案及 Mock 資產可能晚於或早於正式行為，不能單獨作為產品真相。支援來源以 `PROD-006` 為準。

`LIMIT-009` 基準版本的自動化測試主要是範例測試，尚未覆蓋核心產品規則。Gradle 測試成功只代表既有測試通過。

`LIMIT-010` Lint 設定目前為 `abortOnError = false`，所以 `lint` task 成功不代表沒有錯誤。現況證據見 [verification.md](verification.md)。

## AppFunctions 限制

- Gemini 或其他模型不一定會發現本 App；系統是否實際呼叫由平台決定。
- `androidx.appfunctions` 仍是 alpha 版本。
- 跨幣別排序仍受 `LIMIT-001` 限制。
- 各書店第一筆不保證是同一本書，仍受 `LIMIT-004` 限制。
- Instant packaging 目前與 exported Service 共用 application manifest；此處只記錄現況，不新增功能。
