# 導流、分享與 Deep Link

## 商品頁導流

`NAV-001` Compact／單欄模式預設以瀏覽器 Custom Tab 開啟商品連結。

`NAV-002` Compact／單欄且使用者關閉「使用瀏覽器分頁」後，商品連結改在 App 內建 WebView 顯示。

`NAV-003` Medium+／分欄固定在清單旁的詳細區域使用內建 WebView，不受 Custom Tab 偏好影響。App 必須明確使用從 Medium 起提供雙欄的 pane directive，不可依賴 Adaptive navigator 的預設值。原因是現有 Custom Tab 無法嵌入右側詳細區域。

`NAV-004` 內建 WebView 必須提供返回 App 清單的操作，並提供分享目前商品及改用外部瀏覽器開啟的選項。

`NAV-005` 商品分享內容至少包含商品書名與 API 提供的商品連結。實際分享目的地由 Android Sharesheet 與使用者決定。

`openBookProduct` 是系統 agent 入口，不受 `NAV-001` 至 `NAV-004` 的畫面開啟偏好影響；它固定建立系統 VIEW／SEND intent，見 `APPFN-008`。

分欄判定以 Material 3 `WindowSizeClass` 為準，詳見 [known-limitations.md](known-limitations.md) 的 `LIMIT-003`。

## 搜尋快照

`SNAPSHOT-001` 成功搜尋由 API 回傳 `searchId`。有可用 `searchId` 時，App 必須提供複製快照網址及分享快照網址的操作。

`SNAPSHOT-002` 公開快照網址格式為：

```text
https://taiwan-ebook-lover.github.io/searches/{searchId}
```

`SNAPSHOT-003` 快照代表保存並重現當次搜尋結果，不重新搜尋最新價格。書店價格或內容日後改變時，快照不保證更新。

`SNAPSHOT-004` App 收到快照 Deep Link 時，必須以 `searchId` 向 API 取得保存結果。成功取回後，將快照內的原始關鍵字寫入本機搜尋紀錄；失敗時不寫入。

`SNAPSHOT-005` 沒有目前結果或 API 未提供 `searchId` 時，不得分享虛構快照網址，並應提供可觀察提示。

## 關鍵字 Deep Link

`DEEPLINK-001` App 必須處理下列公開關鍵字搜尋格式：

```text
https://taiwan-ebook-lover.github.io/search?q={keyword}
```

`DEEPLINK-002` 關鍵字 Deep Link 代表一次新的即時搜尋，必須套用目前啟用來源、搜尋紀錄、防重複請求與錯誤規則；它不同於保存結果的快照。關鍵字 Deep Link 仍打開 App UI。結構化比價改走 AppFunctions，見 `APPFN-003` 與 `APPFN-005`。

`DEEPLINK-003` App 目前接受 `http` 與 `https`，host 限定 `taiwan-ebook-lover.github.io`，路徑限定 `/search` 與 `/searches/*`。

## 主要實作入口

目前主要入口是 `AndroidManifest.xml`、`BookSearchActivity`、`DeeplinkHelper`、`SimpleWebViewScreen`、`CustomTabSessionManager` 與 `GetSearchSnapshotUseCase`。
