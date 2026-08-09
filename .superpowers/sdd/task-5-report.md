# Task 5 Report — 搜尋紀錄 overlay 與清單大螢幕小修

## 結論

- 不需修改產品程式碼。
- `BookSearchScreen` 將 `BookResultListScreen` 放在 `NavigableListDetailPaneScaffold.listPane` 的 `AnimatedPane` 內。
- 搜尋紀錄卡片與 scrim 都是 `BookResultListScreen` 內層 `Scaffold` content `Box` 的子項；scrim 的 `fillMaxSize()` 只填滿 list pane 邊界，不會覆蓋 Medium+ 的 detail pane WebView。
- `SearchRecords` 與 scrim 的 z-index 僅在該 list content `Box` 內排序，沒有 Activity 或 window 級 overlay。

## 間距檢查

- 結果清單沿用 `search_list_padding_horizontal = 8dp`。
- 廣告卡本身另有 4dp 外距，第一個結果區段標題有 24dp 上距。
- 未發現 wide list pane 上明顯過窄、過寬或區塊黏連問題；依 Task 限制不做推測性 spacing 修改，也未加入 grid 或 maxWidth。

## Spec 對焦

- 符合 `HISTORY-004` 的搜尋紀錄顯示與點擊行為。
- 未改變 `NAV-003` 的大畫面 detail pane 內建 WebView 行為。
- 本次只確認既有 layout scope，沒有產品行為或 Spec 變更。

## 驗證

- `./gradlew :app:compileApiDebugKotlin`
- Journey E 的實機／模擬器互動驗證留待 Task 9；本次結論來自 Compose hierarchy 與 modifier scope 靜態檢查。
