# AppFunctions

本文件描述 Android 16+ 系統 agent 可不打開 App 畫面即可呼叫的裝置端工具。
它不承諾 Gemini 或其他模型一定會發現本 App。

## 登錄與相容

`APPFN-001` App 可向 Android AppFunctions 登錄裝置端工具。系統只在
Android 16+ 索引這些工具。較舊裝置必須仍能安裝並使用現有搜尋 UI。

`APPFN-002` 正式暴露的能力以本文件為準。第一版只包含即時搜尋比價、
讀取搜尋快照、讀取本機最近搜尋紀錄，以及開啟或分享商品連結。

## 搜尋與快照

`APPFN-003` `searchBooks` 接受書名或 ISBN，套用目前啟用來源、無網路
不送出、部分成功，以及價格不換匯。空白輸入必須失敗。相同字串重複
呼叫仍須執行並回傳結果。此規則優於畫面的 `SEARCH-003`。

`APPFN-004` Agent 發起的即時搜尋在 API 送出前寫入本機搜尋紀錄，語意
同 `SEARCH-016`。

`APPFN-005` `getSearchSnapshot` 必須以 `searchId` 讀取保存結果，不得
轉成新的即時搜尋。成功後寫入快照內的原始關鍵字；失敗時不寫入。

`APPFN-006` 回傳給 agent 的商品必要欄位為書名、來源與有效連結。價格
可為未知。未知價不得表示成免費或 `0`。

## 紀錄與開啟商品

`APPFN-007` `getRecentSearchRecords` 只提供此裝置本機最近搜尋字串，
預設最多 10 筆，上限 20 筆，不得一次倒出全部歷史。回傳必須包含
ISO-8601 時間與次數。這不是書庫。

`APPFN-008` `openBookProduct` 只建立系統可啟動的開啟或分享意圖。App
不在此完成付款、登入或購買。

## 非目標與模組邊界

`APPFN-009` 不得把掃描相機、刪除紀錄或設定變更以無確認的 AppFunction
暴露。

`APPFN-010` AppFunction 屬於 `app` 的平台整合。搜尋、快照與紀錄規則
仍由 `commonMain` 領域能力執行。Service 不得呼叫 ViewModel。

## 主要實作入口

目前入口是 `BaseEbookAppFunctionService`、四個 `@AppFunctionSerializable`
DTO，以及 `SearchBooksUseCase`、`GetSlicedSearchSnapshotUseCase`、
`GetRecentSearchRecordsUseCase`。這些是追查路徑，不是不可替換契約。
