# BookSearch Adaptive UI — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 以官方 Material 3 Adaptive 重接 `BookSearch` list–detail，讓 Desktop／大螢幕分欄與 Custom Tab 導流單一真相，並完成主流程畫面整理。

**Architecture:** `BookSearchScreen` 使用 `NavigableListDetailPaneScaffold` + `rememberListDetailPaneScaffoldNavigator<Book>()`；以 `currentWindowAdaptiveInfo().windowSizeClass` 判定 Compact 與否，驅動 Custom Tab vs in-app detail。移除自訂 `isTabletSize`。ViewModel 不持有 pane 狀態。

**Tech Stack:** Jetpack Compose、Material 3 Adaptive（`adaptive` / `adaptive-layout` / `adaptive-navigation`）、既有 `Book`（`@Parcelize`）、Custom Tabs、`compose-webview`

**權威文件：** [Spike](../spike/2026-08-09-compose-adaptive-ui-desktop.md) · [task_plan.md](./task_plan.md) · [design_guidelines.md](./design_guidelines.md) · [user_journey.md](./user_journey.md)

## Global Constraints

- 主範圍僅 `BookSearch` 主流程；次要 Activity／FoldingFeature／maxWidth／滑鼠鍵盤為 Follow-up。
- Compact 單欄、Medium+ 分欄（官方預設）；600–840dp 行為變更為 deliberate。
- 維持 `NAV-001`–`NAV-004`。
- 使用者可見字串與重要 contentDescription：en / zh-TW / zh-CN，不硬編碼。
- CI 基線：`./gradlew test lint assembleApiDebug`（JDK 17）；不宣稱零 Lint。
- **Commit：** 僅在使用者明確要求時執行；下列 Commit 步驟為建議訊息草稿。
- Commit 訊息底部加：`Assisted-by: Cursor:Grok-4.5`（見 repo 規則）。
- 執行時更新 [progress.md](./progress.md)、[findings.md](./findings.md)。

## File map

| 路徑 | 動作 | 責任 |
| --- | --- | --- |
| `app/.../booksearch/util/WindowWidthAdaptive.kt`（建議新建） | Create | Compact／Medium+ 判定 helper |
| `app/.../booksearch/BookSearchScreen.kt` | Modify | Navigable scaffold、focus back、傳出 `isWidthCompact` |
| `app/.../booksearch/BookSearchActivity.kt` | Modify | Custom Tab 分支改接 Compact |
| `app/.../ui/theme/EBookTheme.kt` | Modify | 移除 `LocalDeviceInfo`／`isTabletSize`（確認無引用後） |
| `app/.../booksearch/BookResultListScreen.kt` | Modify | overlay／a11y／必要 chrome |
| `app/.../simplewebview/SimpleWebViewScreen.kt` | Modify | a11y；必要時關閉行為 |
| `app/.../booksearch/composable/DetailPaneEmptyState.kt`（建議） | Create | 分欄空狀態抽出 |
| `app/src/main/res/values/strings.xml` 等三語系 | Modify | contentDescription 字串 |
| `app/src/androidTest/.../BookSearchAdaptiveNavigationTest.kt`（建議） | Create | Compact vs Medium+ 導流測試 |
| `docs/spec/known-limitations.md` | Modify | `LIMIT-003`（Task 8，需核准） |
| `docs/spec/navigation-sharing-deep-links.md` | Modify | NAV 用語（Task 8，需核准） |

---

### Task 1: Compact 判定 helper + Activity 導流改接

**Files:**
- Create: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/util/WindowWidthAdaptive.kt`
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchActivity.kt`

**Interfaces:**
- Produces: `@Composable fun isWindowWidthCompact(): Boolean`（或非 Composable 的 `WindowSizeClass.isWidthCompact()` extension）
- Produces: `onBookSearchItemClick` 參數由 `isTabletSize: Boolean` 改為 `isWidthCompact: Boolean`（語意：`true` = Compact 單欄寬度）

- [ ] **Step 1: 確認專案 Adaptive API 符號**

在實作機查目前 BOM 下可用 API（擇一對齊官方文件）：

```kotlin
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
// 常見寫法（實作時以編譯器／文件為準）:
// val widthCompact = !windowSizeClass.isWidthAtLeastBreakpoint(
//     WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
// )
```

若符號名稱不同，以能編譯的官方等效 API 為準，並記入 `findings.md`。

- [ ] **Step 2: 新增 helper**

```kotlin
package liou.rayyuan.ebooksearchtaiwan.booksearch.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Composable
fun isWindowWidthCompact(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return !windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
    )
}
```

（若 `WindowSizeClass` 套件／常數名不同，改成專案可編譯版本，保持「未達 Medium = Compact」語意。）

- [ ] **Step 3: 更新 `BookSearchScreen` 回呼簽名**

將：

```kotlin
onBookSearchItemClick: (book: Book, paneNavigator: ThreePaneScaffoldNavigator<Book>, isTabletSize: Boolean) -> Unit
```

改為：

```kotlin
onBookSearchItemClick: (book: Book, paneNavigator: ThreePaneScaffoldNavigator<Book>, isWidthCompact: Boolean) -> Unit
```

內部：

```kotlin
val isWidthCompact = isWindowWidthCompact()
// ...
onBookSearchItemClick = { onBookSearchItemClick(it, paneNavigator, isWidthCompact) }
```

暫時可仍使用既有 `ListDetailPaneScaffold`（Task 2 再遷移）。刪除對 `LocalDeviceInfo` 的讀取。

- [ ] **Step 4: 更新 `BookSearchActivity` 分支**

```kotlin
onBookSearchItemClick = { book, paneNavigator, isWidthCompact ->
    if (userPreferenceManager.isPreferCustomTab() && isWidthCompact) {
        openInCustomTab(book.asUiModel().getLink())
    } else {
        scope.launch {
            paneNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, book)
        }
    }
    // ...既有 rank 計數邏輯不變
}
```

- [ ] **Step 5: 編譯確認**

Run: `./gradlew :app:compileApiDebugKotlin`

Expected: SUCCESS

- [ ] **Step 6: 更新 progress；建議 commit（需使用者同意）**

```text
refactor(booksearch): gate Custom Tab on WindowSizeClass Compact

Replace isTabletSize orientation thresholds so pane layout and product
navigation share one adaptive width source.
```

---

### Task 2: `NavigableListDetailPaneScaffold` 遷移與返回／focus

**Files:**
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`

**Interfaces:**
- Consumes: Task 1 的 `isWindowWidthCompact()` / `isWidthCompact` 回呼
- Produces: 使用 `NavigableListDetailPaneScaffold(navigator = paneNavigator, ...)`；搜尋框 focus 時仍優先消費 back

- [ ] **Step 1: 替換 scaffold**

將 `ListDetailPaneScaffold(directive = ..., value = ..., listPane = ..., detailPane = ...)` 改為：

```kotlin
NavigableListDetailPaneScaffold(
    navigator = paneNavigator,
    modifier = modifier,
    listPane = { /* 既有 AnimatedPane + BookResultListScreen */ },
    detailPane = { /* 既有 AnimatedPane + WebView / empty */ }
)
```

import 來自 `androidx.compose.material3.adaptive.navigation`（以專案依賴為準）。

- [ ] **Step 2: 收斂 BackHandler**

`NavigableListDetailPaneScaffold` 已處理 pane predictive back。保留**僅**處理搜尋框 focus 的邏輯，例如：

```kotlin
BackHandler(enabled = isTextInputFocused) {
    bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
}
```

若實機發現 pane back 失效，再依官方加上 `ThreePaneScaffoldPredictiveBackHandler` 或恢復 `paneNavigator.canNavigateBack()` 分支，並寫入 `findings.md`。

- [ ] **Step 3: detail 關閉鈕條件**

維持：

```kotlin
val isDetailPaneVisible =
    paneNavigator.scaffoldValue.secondary == PaneAdaptedValue.Expanded
// showCloseButton = !isDetailPaneVisible
```

關閉時：

```kotlin
scope.launch {
    if (paneNavigator.canNavigateBack()) {
        paneNavigator.navigateBack()
    }
}
```

- [ ] **Step 4: 編譯**

Run: `./gradlew :app:compileApiDebugKotlin`

Expected: SUCCESS

- [ ] **Step 5: 建議 commit（需使用者同意）**

```text
refactor(booksearch): adopt NavigableListDetailPaneScaffold

Align list-detail navigation with Material 3 adaptive canonical APIs
while keeping search-field focus back behavior.
```

---

### Task 3: 移除 `isTabletSize` / `LocalDeviceInfo`

**Files:**
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/ui/theme/EBookTheme.kt`
- Grep 全專案確認無剩餘引用

**Interfaces:**
- Produces: `EBookTheme` 不再提供 `LocalDeviceInfo`

- [ ] **Step 1: 搜尋殘留**

Run: `rg "isTabletSize|LocalDeviceInfo|DeviceInfo" --glob '*.kt'`

Expected: 僅剩 `EBookTheme.kt` 定義（或已無）

- [ ] **Step 2: 刪除**

自 `EBookTheme.kt` 移除：

- `LocalDeviceInfo`
- `DeviceInfo`
- `isTabletSize()`
- `CompositionLocalProvider` 中對 `LocalDeviceInfo` 的提供

保留 color／drawable CompositionLocals 與 `MaterialTheme`。

- [ ] **Step 3: 編譯**

Run: `./gradlew :app:compileApiDebugKotlin`

Expected: SUCCESS

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
refactor(ui): remove unused isTabletSize device info

WindowSizeClass now owns adaptive width decisions for book search.
```

---

### Task 4: Detail 空狀態與關閉鈕 chrome

**Files:**
- Create（建議）: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/composable/DetailPaneEmptyState.kt`
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`

**Interfaces:**
- Produces: `@Composable fun DetailPaneEmptyState(modifier: Modifier = Modifier)`

- [ ] **Step 1: 抽出空狀態**

將 `BookSearchScreen` detail 的「無 book」分支（目前 `Scaffold` + 半透明大 icon）移到 `DetailPaneEmptyState`，保持：

- `WindowInsets.safeDrawing`
- 圓角／`pale_slate`（或 theme 對應色）
- icon alpha 適中，分欄右側看起來像待機面板而非壞掉頁面

- [ ] **Step 2: 接回 detailPane**

```kotlin
if (book != null) {
    SimpleWebViewScreen(...)
} else {
    DetailPaneEmptyState(modifier = Modifier.fillMaxSize())
}
```

- [ ] **Step 3: 目視檢查清單（實機／模擬器）**

- Medium+ 未選書：右側空狀態正常
- Compact 不應無故顯示怪異空頁（無 destination 時以 list 為主）

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
ui(booksearch): polish list-detail empty detail pane

Extract a dedicated empty state for the adaptive detail pane.
```

---

### Task 5: 搜尋紀錄 overlay 與清單大螢幕小修

**Files:**
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookResultListScreen.kt`
- 視需要: `app/.../booksearch/composable/SearchRecords.kt`、結果 list composable

**Interfaces:**
- Produces: overlay 僅覆蓋 list pane 內容區（隨 `BookResultListScreen` 邊界，不逃出 pane）

- [ ] **Step 1: 確認 overlay 範圍**

`BookResultListScreen` 的 scrim／`SearchRecords` 已在 list pane 的 `AnimatedPane` 內則通常已隔離。若仍用全螢幕感覺擋住 detail：

- 確保遮罩 `Modifier` 綁在 list `Box`/`Scaffold` content，而非外層 Activity 全螢幕 overlay。

- [ ] **Step 2: 清單可讀性小修（僅明顯問題）**

在 Medium+ 檢查：

- 水平 padding（`R.dimen.search_list_padding_horizontal`）是否過窄／過怪
- 廣告橫幅與結果區塊間距

只做小幅 spacing 調整；**不加** grid、**不加** maxWidth。

- [ ] **Step 3: 手測 Journey E（分欄 + 搜尋紀錄）**

Expected: 開啟紀錄時仍可看到／操作 detail（或至少不被半透明全螢幕鎖死）

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
fix(booksearch): keep search-record overlay inside list pane

Prevent the dimmed search-history layer from blocking the detail pane
on medium and wider windows.
```

---

### Task 6: 主流程 contentDescription 字串資源化

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-zh-rTW/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`
- Modify: `BookResultListScreen.kt`、`SimpleWebViewScreen.kt`
- 視需要: `BookSearchResultScreen.kt`、`SearchRecords.kt`、`BookSearchItem.kt`（主流程相關者）

**Interfaces:**
- Produces: 字串資源 key（名稱可微調，但三語系必須齊）：

| key（建議） | en | zh-TW | zh-CN |
| --- | --- | --- | --- |
| `content_desc_option_menu` | Option menu | 選項選單 | 选项菜单 |
| `content_desc_close_detail` | Close | 關閉 | 关闭 |
| `content_desc_book_cover` | Book cover | 書封 | 书封 |
| `content_desc_clear_search_record` | Clear search record | 清除搜尋紀錄 | 清除搜索记录 |
| `content_desc_scroll_to_top` | Scroll to top | 回到頂端 | 回到顶部 |

（`bottom button` 若實為 scroll-to-top，用語意正確的描述。）

- [ ] **Step 1: 加入三語系字串**
- [ ] **Step 2: 替換主流程硬編碼 `contentDescription = "..."`**
- [ ] **Step 3: 編譯**

Run: `./gradlew :app:compileApiDebugKotlin`

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
a11y(booksearch): localize content descriptions on main flow

Replace hardcoded TalkBack labels with en, zh-TW, and zh-CN resources.
```

---

### Task 7: Compose UI 測試（導流分支）

**Files:**
- Create: `app/src/androidTest/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchAdaptiveNavigationTest.kt`
- 視需要抽出可測 callback／小 wrapper 以便不啟動全 Activity 廣告／網路

**Interfaces:**
- Produces: 至少兩個測試——Compact 偏好 Custom Tab 不 navigate detail；非 Compact 會 navigate detail

> 若全 Activity 測試過重，改測純 Compose 宿主：傳入 fake `onBookSearchItemClick` 記錄參數，用 `Modifier.width(...)` 或測試用 Adaptive 設定模擬寬度。實作時選**能穩定跑在 CI** 的最小方案，細節記入 `findings.md`。

- [ ] **Step 1: 寫失敗測試（契約）**

偽代碼目標：

```kotlin
@Test
fun compact_withCustomTabPreference_doesNotNavigateToDetail() {
    // arrange: width compact, preferCustomTab = true
    // act: click book
    // assert: openCustomTab called; detail navigation not called
}

@Test
fun mediumWidth_ignoresCustomTabPreference_navigatesToDetail() {
    // arrange: width medium+, preferCustomTab = true
    // act: click book
    // assert: navigateTo Detail with book; custom tab not opened
}
```

- [ ] **Step 2: 跑測試確認失敗／或先紅燈**

Run: `./gradlew :app:connectedApiDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchAdaptiveNavigationTest`

（若環境無裝置，改用可在 JVM 跑的最小契約測試，或記錄「待 CI／本機裝置」於 `progress.md`。）

- [ ] **Step 3: 實作到綠燈（必要時加測試替身）**
- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
test(booksearch): cover Custom Tab gating by window width

Add instrumentation coverage for compact vs medium+ navigation branches.
```

---

### Task 8: Spec patch 套用（需核准）

**Files:**
- Modify: `docs/spec/known-limitations.md`（`LIMIT-003`）
- Modify: `docs/spec/navigation-sharing-deep-links.md`（`NAV-001`–`NAV-003` 用語）

**前置：** 使用者明確同意套用 Spec。

- [ ] **Step 1: 依 Spike §6 改寫 `LIMIT-003`**
- [ ] **Step 2: NAV 用語改 Compact／Medium+**
- [ ] **Step 3: 跑 align-code-and-spec skill 做對焦（唯讀 audit → 確認一致）**
- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
docs(spec): align adaptive breakpoints with WindowSizeClass

Document Compact vs Medium+ list-detail behavior and retire orientation
based tablet thresholds from LIMIT-003.
```

---

### Task 9: 手動驗證與收尾

**Files:**
- Update: `ai-knowledge/refactor_home/progress.md`
- Update: `ai-knowledge/refactor_home/findings.md`（若有坑）

- [ ] **Step 1: 跑基線 Gradle**

Run: `./gradlew test lint assembleApiDebug`

Expected: 建置成功；記錄已知 Lint 不阻擋（勿宣稱零問題）

- [ ] **Step 2: 手動旅程（對照 user_journey.md）**

- [ ] Journey A Compact + Custom Tab
- [ ] Journey B Compact + in-app WebView
- [ ] Journey C Medium+ 分欄
- [ ] Journey D Desktop 縮放保留 detail
- [ ] Journey E 搜尋紀錄 overlay
- [ ] Journey F 快照／設定回歸

- [ ] **Step 3: 更新 `progress.md` 里程碑與驗證 log**
- [ ] **Step 4: 向使用者回報完成範圍與未驗證項**

---

## Manual verification checklist（彙整）

- [ ] Compact + Custom Tab 開 → 外開，不進 detail
- [ ] Compact + Custom Tab 關 → 單欄 WebView，返回回清單
- [ ] Medium／Expanded → 分欄；點書右側 WebView；偏好不影響
- [ ] Desktop 可調視窗跨 Compact↔Medium↔Expanded
- [ ] 已選書縮窄 → 仍顯示該書；返回回清單
- [ ] 未選書分欄 → 右側空狀態
- [ ] 搜尋紀錄 overlay 不擋 detail
- [ ] 手機直向回歸
- [ ] contentDescription 已資源化（抽樣）

## Self-review（寫 plan 時已對 Spike）

| Spike 要求 | Task |
| --- | --- |
| Navigable + WindowSizeClass | 1–2 |
| 移除 isTabletSize | 1、3 |
| 縮放保留 detail | 2（navigator 預設）+ 9 手測 |
| Chrome／overlay／a11y | 4–6 |
| UI 測試 + 手測 | 7、9 |
| Spec patch | 8 |
| Follow-up 不實作 | Global Constraints |

## Execution handoff

Plan 已拆分並存放於 `ai-knowledge/refactor_home/`。

實作時可選：

1. **Subagent-Driven（建議）** — 每 Task 新 subagent，Task 間審查  
2. **Inline Execution** — 本會話依 `executing-plans` 批次執行並設檢查點  

開始實作前請使用者明確選擇，並確認是否允許在各 Task 結束時 commit。
