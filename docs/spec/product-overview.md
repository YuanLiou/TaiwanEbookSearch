# 產品總覽

## 產品目的

`PROD-001` 台灣電子書搜尋必須協助台灣電子書讀者以書名或 ISBN 一次搜尋多家線上書店，快速比較可購買來源與價格。

`PROD-002` App 的責任到搜尋、比較、顯示供應狀態與導向原書店為止。帳號登入、付款、購買、書庫管理及閱讀由原書店負責，不屬於本產品。

`PROD-003` 目前只承諾書名與 ISBN 查詢。作者、出版社或其他自由文字即使可能取得結果，也不屬於保證支援的搜尋方式。

## 平台與發行

`PROD-004` 本產品是 Android App，正式產品基準為 `apiRelease`，並以 Google Play 發行流程為主。

`PROD-005` 目前最低相容版本為 Android 8.0（API 26）。Target SDK 與 Compile SDK 是會隨維護更新的建置設定，不是長期產品契約。

## 支援來源

`PROD-006` 目前支援下列 10 個搜尋來源：

| 內部識別 | 顯示名稱 |
| --- | --- |
| `readmoo` | Readmoo |
| `kindle` | Amazon Kindle |
| `kobo` | Kobo |
| `bookWalker` | BOOKWALKER |
| `booksCompany` | 博客來 |
| `taaze` | TAAZE 讀冊生活 |
| `playStore` | Google Play 圖書 |
| `pubu` | Pubu |
| `hyread` | HyRead 電子書 |
| `likerLand` | Liker Land |

個別來源可以維修、失敗或由使用者停用，不代表 App 整體不可使用。

## 首頁與服務狀態

`PROD-007` App 啟動且尚未顯示搜尋結果時，必須向 API 取得來源狀態並顯示每個來源目前為服務正常、維修中或由使用者關閉。

`PROD-008` 服務狀態首頁同時顯示 App 版本與 AdMob 橫幅廣告。搜尋結果頁也顯示橫幅廣告。

## 語系與外觀

`PROD-009` 正式支援英文、台灣繁體中文與中國簡體中文。新增或修改使用者可見文字時，三種語系都必須有對應資源。

`PROD-010` 正式支援明亮、深色及跟隨系統主題。主題設定細節見 [history-and-settings.md](history-and-settings.md)。

## 評分邀請

`PROD-011` 使用者累計開啟商品結果 5 次後，App 可以觸發一次 Google Play App 內評分流程。已顯示狀態保存在裝置上，之後不重複要求。

Google Play 是否實際顯示評分 UI 仍由平台 API 決定；App 只負責在符合條件時提出請求。

## 非目標

Web 版、搜尋後端、快照保存後端、各書店網站與 F-Droid 發行不在本 repository 的正式規格範圍內。App 只記錄對它們的必要外部契約。

AppFunctions 是 Android 16+ 的可選系統入口，不擴大 `PROD-002`。它不新增購買、登入、書庫或閱讀能力。
