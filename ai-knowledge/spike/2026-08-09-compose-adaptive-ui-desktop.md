# Spike: Jetpack Compose Adaptive UI（含 Android Desktop Mode）

狀態：設計已對齊（待實作）  
日期：2026-08-09  
分支脈絡：`enhancement/compose`  
性質：**Spike only**（本文件為研究與設計結論；不含實作）

## 1. 目標

讓 Compose 搜尋主流程具備完整、可維護的 Adaptive UI，在手機、平板與 **Android Desktop Mode（可調視窗）** 都能正常使用，並以較少更動對齊 Material 3 官方 Adaptive 作法。

### 成功標準（主範圍）

- 視窗變寬時自動分欄（清單｜商品 WebView）；變窄時回到單欄。
- 維持 `NAV-001`–`NAV-004`：單欄可依偏好使用 Custom Tab；分欄固定右側 in-app WebView。
- 視窗縮放過程中狀態不丟：已選商品、pane 導航、清單捲動等行為合理。

### 非目標（主範圍）

- 次要 Activity（設定、書店排序、相機）的 Adaptive 實作。
- 完整滑鼠／鍵盤互動（hover、右鍵、快捷鍵）。
- 清單閱讀寬度（`maxWidth`）。
- FoldingFeature／鉸鏈佈局實作。
- 單 Activity + NavigationSuite 大重構。

上述項目見 [§8 Follow-up](#8-follow-up)。

## 2. 決策紀錄（Interview 結論）

| # | 議題 | 決定 |
| --- | --- | --- |
| 1 | 範圍 | 主流程 `BookSearch` only；次要畫面列 Follow-up |
| 2 | Desktop「正常使用」 | 自適應分欄 + 狀態保留；滑鼠／鍵盤列 Follow-up |
| 3 | 分欄／導流真相來源 | Material 3 `WindowSizeClass`／官方 Adaptive API |
| 4 | 斷點 | 官方預設：Compact 單欄；Medium+ 分欄 |
| 5 | 超寬內容寬度 | 先撐滿 pane；清單 `maxWidth` 列 Follow-up |
| 6 | 分欄→單欄且已選書 | 保留 detail；返回回清單 |
| 7 | Spec | Spike 附 patch 草稿；不直接改基線 |
| 8 | `isTabletSize` | 主流程移除；不再用於導流 |
| 9 | 驗證 | 手動清單 + 建議 Compose UI 測試 |
| 10 | Foldable | Spike 研究；實作列 Follow-up |
| 11–12 | 畫面整理 | Adaptive 骨架 + 主流程 chrome／可讀性／搜尋層／WebView 整理；明顯 a11y 才修 |
| 13 | 實作路線 | **方案 2：官方 Canonical 對齊** |

## 3. 現況盤點

### 已具備

- 依賴：`compose-adaptive`、`adaptive-layout`、`adaptive-navigation`（見 `gradle/libs.versions.toml`、`app/build.gradle.kts`）。
- `BookSearchScreen` 已使用 `ListDetailPaneScaffold` + `rememberListDetailPaneScaffoldNavigator<Book>()` + 手動 `BackHandler`。
- Detail 為 `SimpleWebViewScreen`；未選書時有 placeholder。
- 產品規則 `NAV-001`–`NAV-004` 已文件化；`LIMIT-003` 註明大畫面判定非永久契約。

### 問題

1. **雙重判定**：scaffold 走 Adaptive directive，Custom Tab 卻看自訂 `isTabletSize()`（直向 >600dp、橫向 >840dp），Desktop 縮放時可能漂移。
2. **非官方 API 表面**：手組 `ListDetailPaneScaffold` + 手動 back，未用 `NavigableListDetailPaneScaffold`／`currentWindowAdaptiveInfo()`。
3. **主流程大螢幕 polish 不足**：空狀態、overlay、AppBar 分工、字串資源等仍偏手機假設。
4. **相關債**：`BookSearchScreen` 內 rank-app 有 `FIXME`（邏輯與原版不一致）——非 Adaptive 核心，列 Follow-up／實作前再定。

### 相關程式入口

- `BookSearchActivity.kt` — Custom Tab vs `navigateTo(Detail)`
- `BookSearchScreen.kt` — list–detail scaffold
- `BookResultListScreen.kt` / `SimpleWebViewScreen.kt` — list／detail UI
- `EBookTheme.kt` — `LocalDeviceInfo` / `isTabletSize()`
- Spec：`docs/spec/navigation-sharing-deep-links.md`、`docs/spec/known-limitations.md`（`LIMIT-003`）

## 4. 方案比較與採用

| 方案 | 摘要 | 結論 |
| --- | --- | --- |
| 1 最小演化 | 保留現有 scaffold，只換導流判定 | 改動更小，但與官方範例持續落差 |
| **2 Canonical 對齊** | `NavigableListDetailPaneScaffold` + `currentWindowAdaptiveInfo()` + 主流程整理 | **採用** |
| 3 大重構 | 單 Activity／NavigationSuite 等 | 超出範圍；列 Follow-up |

## 5. 建議設計

### 5.1 架構

```text
BookSearchActivity
  └─ EBookTheme（不再提供 isTabletSize）
       └─ BookSearchScreen
            ├─ currentWindowAdaptiveInfo() → WindowSizeClass
            ├─ rememberListDetailPaneScaffoldNavigator<Book>()
            └─ NavigableListDetailPaneScaffold
                 ├─ listPane  → BookResultListScreen
                 └─ detailPane → SimpleWebViewScreen 或空狀態
```

| 層 | 責任 |
| --- | --- |
| Adaptive scaffold | 單欄／分欄、pane 導航、縮放保留 destination |
| `WindowSizeClass` | Custom Tab 門檻（僅 Compact 可依偏好） |
| Activity | Custom Tab／分享／設定／相機；點書分支 |
| ViewModel | 搜尋狀態與捲動等；不承載 pane UI 狀態 |
| 移除 | `DeviceInfo.isTabletSize`／`isTabletSize()`（主流程） |

### 5.2 導流與縮放

**點選商品**

| 視窗 | Custom Tab 偏好 | 行為 |
| --- | --- | --- |
| Compact | 開 | Custom Tab（不 push detail） |
| Compact | 關 | `navigateTo(Detail, book)` |
| Medium / Expanded | 任意 | 一律 in-app detail（`NAV-003`） |

**縮放**

- Medium+ → Compact：保留已選 detail；返回才回清單。
- Compact → Medium+：有 destination 則並排；無則右側空狀態。

**返回**

- 交給 `NavigableListDetailPaneScaffold`／navigator（含 predictive back）。
- 搜尋框 focus 時先取消 focus（維持現況）。
- 單欄 detail 顯示關閉按鈕；雙欄 detail 可見時隱藏（維持現況語意，改掛 scaffold value）。

**Deliberate change**

- 橫向／Desktop 約 **600–840dp**：舊 `isTabletSize` 偏單欄 → 新邏輯 **分欄 + 右側 WebView**。

### 5.3 主流程畫面整理

1. **雙欄 chrome**：空狀態、返回／關閉、兩側 TopAppBar 分工。
2. **清單可讀性**：間距／層級／廣告在 Medium+ 的觀感；不改資訊架構、不加書卡 grid；內容仍撐滿 pane。
3. **搜尋框／紀錄**：SearchBox 留在 list pane；overlay 不應誤擋 detail。
4. **WebView detail**：單欄／分欄標題列、progress、選單一致；選書切換載入可預期。
5. **a11y（明顯處）**：硬編碼 contentDescription 改字串資源（en／zh-TW／zh-CN）。

### 5.4 刻意不變

- 多 Activity 架構。
- `NAV-001`–`NAV-004` 產品語意（只改判定來源）。
- pane 內 `fillMaxWidth()`（不做 maxWidth）。

## 6. Spec patch 草稿（尚未套用）

> 實作或核准同步時再改 `docs/spec`；以下為建議文案方向。

### 6.1 `docs/spec/known-limitations.md` — `LIMIT-003`

**現況（摘要）**  
大畫面判定使用螢幕方向與寬度門檻：直向 >600dp，橫向 >840dp。

**建議改寫方向**

```markdown
`LIMIT-003` 大畫面／分欄判定以 Material 3 Adaptive 的 `WindowSizeClass`
（與 `ListDetailPaneScaffold`／`NavigableListDetailPaneScaffold` 預設 directive）為準：
Compact 為單欄，Medium 及以上為分欄。

此判定來源可隨官方 Adaptive API 演進而調整，但必須維持
`NAV-001` 至 `NAV-004`：單欄可用 Custom Tab 偏好，分欄則在詳細區域顯示 App 內商品頁。
```

### 6.2 `docs/spec/navigation-sharing-deep-links.md`

| ID | 建議調整 |
| --- | --- |
| `NAV-001` | 「手機單欄」→「Compact／單欄」 |
| `NAV-002` | 同上語境（單欄 + 關閉 Custom Tab 偏好 → WebView） |
| `NAV-003` | 「大畫面分欄」→「Medium+／分欄（官方 Adaptive 預設）」 |
| 文末 | 大畫面判定改指向更新後的 `LIMIT-003` |

不強制新增 NAV ID；若實作發現語意缺口再補。

### 6.3 行為變更說明（應寫進 PR／Spec 變更說明）

- **誰受影響**：寬度約 600–840dp 的橫向手機／小視窗 Desktop／部分平板姿態。
- **舊**：較可能單欄 + 可 Custom Tab。
- **新**：分欄 + 右側 WebView（忽略 Custom Tab 偏好）。
- **理由**：對齊官方 List-Detail 預設，避免雙重門檻。

## 7. 驗證計畫

### 7.1 手動（實作後必要）

- [ ] Compact + Custom Tab 開：外開分頁，不進 in-app detail
- [ ] Compact + Custom Tab 關：單欄 WebView，返回回清單
- [ ] Medium／Expanded：分欄；點書右側 WebView；偏好不影響
- [ ] Desktop 可調視窗：跨 Compact↔Medium↔Expanded 反覆縮放
- [ ] 已選書時縮窄：仍顯示該書；返回回清單
- [ ] 未選書分欄：右側空狀態可用
- [ ] 搜尋紀錄 overlay：不阻擋 detail 操作
- [ ] 手機直向回歸：搜尋、結果、快照選單、設定入口
- [ ] 硬編碼 contentDescription 已資源化（抽樣）

### 7.2 Compose UI 測試（建議）

- Compact 且偏好 Custom Tab：點書不進入 detail destination（或驗證 Activity 分支／callback 契約）。
- Medium+：點書後 navigator 位於 Detail 且帶 `Book` content key。
- （可選）不同 width 下 pane visibility 符合 Compact vs Medium+。

說明：自動化難以完整替代真 Desktop；手動量窗仍是權威證據。Gradle 綠燈≠行為驗收（見 `LIMIT-009`）。

## 8. Follow-up

### 8.1 產品／UI

| 項目 | 說明 | 優先 |
| --- | --- | --- |
| 次要畫面 Adaptive | 設定、書店排序；相機另評估 | 中 |
| 滑鼠／鍵盤 | hover、右鍵、快捷鍵 | 低～中 |
| 清單 `maxWidth` | pane 很寬時的閱讀寬度／置中 | 低 |
| 完整 a11y | 全面字串／焦點／語意 | 中 |
| Rank-app `FIXME` | 與原版評分邏輯對齊 | 中（可與主 PR 分開） |

### 8.2 FoldingFeature（研究結論 → 實作 Follow-up）

**研究摘要**

- 官方建議透過 `WindowInfoTracker`／`WindowLayoutInfo` 讀取 `FoldingFeature`，區分 flat、book、tabletop（例如 `HALF_OPENED` + horizontal = tabletop）。
- Compose 文件亦提到依 posture 切佈局；List-Detail 在一般大螢幕已可由 `WindowSizeClass` 覆蓋多數「左右分欄」需求。
- 鉸鏈／tabletop 的額外價值多在：**避免重要內容畫在鉸鏈上**、或 **上／下分割（tabletop）** 而非僅左右 list–detail。

**建議後續方向（不實作於本次）**

1. 主範圍先把 `WindowSizeClass` + `NavigableListDetailPaneScaffold` 做穩（已覆蓋 Desktop／多數平板）。
2. Follow-up 再引入 `WindowLayoutInfo`：
   - tabletop：評估「上：清單／下：detail」或保持左右但避開 fold bounds；
   - book／vertical hinge：確認分隔線與 pane 比例不被鉸鏈切開。
3. 實機矩陣以摺疊機 + Desktop 分開驗證；勿用摺疊邏輯反向複雜化 Desktop。

### 8.3 架構型

- 單 Activity + NavigationSuite／統一 adaptive shell（原方案 3）——僅在次要畫面也全面 Adaptive 後再評估。

## 9. 實作備忘（供後續 plan，非本次編碼）

建議順序：

1. 引入 `currentWindowAdaptiveInfo()`；點書分支改 Compact 判定。
2. `ListDetailPaneScaffold` → `NavigableListDetailPaneScaffold`；收斂手動 `BackHandler`。
3. 移除主流程對 `LocalDeviceInfo.isTabletSize`／`isTabletSize()` 的依賴（若全專案無引用可刪除 CompositionLocal）。
4. Chrome／空狀態／overlay／字串資源整理。
5. 手動 + 建議 UI 測試。
6. 經核准後套用 Spec patch（`LIMIT-003`、NAV 用語）。

依賴：已使用 `@OptIn(ExperimentalMaterial3AdaptiveApi::class)`；升級 BOM／adaptive 時需回歸 navigator API。

## 10. 風險

| 風險 | 緩解 |
| --- | --- |
| 600–840dp 行為變更 | Spec／PR 明示；手動覆蓋該寬度 |
| Experimental Adaptive API 變動 | 鎖在現有 BOM；升級時專測 list–detail |
| `Navigable*` 遷移回歸 | 保留 Compact Custom Tab／WebView 雙路徑手測 |
| Overlay 擋 detail | 實作時明確 zIndex／範圍在 list pane |
| 範圍膨脹 | Follow-up 清單擋下摺疊／鍵盤／次要頁 |

## 11. 參考

- [Canonical layouts — List-detail](https://developer.android.com/develop/ui/compose/layouts/adaptive/canonical-layouts)
- [List-detail guidance](https://developer.android.com/develop/ui/compose/layouts/adaptive/list-detail)
- [Make your app fold-aware](https://developer.android.com/develop/ui/compose/layouts/adaptive/foldables/make-your-app-fold-aware)
- 專案 Spec：`docs/spec/navigation-sharing-deep-links.md`、`docs/spec/known-limitations.md`

---

## 核准

- [x] 訪談決策對齊  
- [x] 方案 2 採用  
- [x] 設計 §1–§4 核准  
- [ ] 本 Spike 文件審閱（待負責人）  
- [ ] Spec patch 套用（另指令）  
- [ ] 實作 plan／開工（另指令）
